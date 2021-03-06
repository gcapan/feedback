package com.discovery.feedback.model.history;

import org.apache.mahout.cf.taste.impl.common.AbstractLongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.common.ClassUtils;
import org.apache.mahout.common.Pair;
import org.apache.mahout.math.*;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CooccurrenceCountSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.VectorSimilarityMeasure;
import org.apache.mahout.math.list.DoubleArrayList;
import org.apache.mahout.math.list.LongArrayList;
import org.apache.mahout.math.map.OpenLongDoubleHashMap;
import org.apache.mahout.math.map.OpenLongIntHashMap;
import org.apache.mahout.math.set.OpenLongHashSet;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@author} gcapan
 */
public class History {
  private Matrix history;
  private OpenLongIntHashMap idMap;
  private LongArrayList ids;
  int index = 0;

  public History(int maxNoOfEntities, int columnSize) {
    ids = new LongArrayList(maxNoOfEntities);
    history = new SparseRowMatrix(maxNoOfEntities, columnSize);
    idMap = new OpenLongIntHashMap(maxNoOfEntities);

  }

  public int getNumEntities(){
    return ids.size();
  }

  public LongPrimitiveIterator allFroms() {
    return new LongArrayListIterator();
  }

  public Iterable<Pair<Long, Vector.Element>> allPreferences() {
    return new AllIterable();
  }

  public Iterable<MatrixSlice> allEntities() {
    return new MatrixIterable();
  }

  public Vector getPreferencesFrom(long id, boolean createNew) {
    Vector v = history.viewRow(idMap.get(id));
    return createNew ? v.clone() : v;
  }

  public FastIDSet getIdsFrom(long id) {
    Vector v = history.viewRow(idMap.get(id));
    FastIDSet idSet = new FastIDSet(v.getNumNonZeroElements());
    for (Vector.Element e : v.nonZeroes()) {
      idSet.add(e.index());
    }
    return idSet;
  }

  public void sortByValue(long id, LongArrayList ids, DoubleArrayList values) {
    Vector v = history.viewRow(idMap.get(id));
    OpenLongDoubleHashMap vAsMap = new OpenLongDoubleHashMap(v.getNumNonZeroElements());
    for(Vector.Element e:v.nonZeroes()) {
      vAsMap.put(e.index(), e.get());
    }
    vAsMap.pairsSortedByValue(ids, values);
  }

  public int getCommons(long id1, long id2) {
    return (int) similarity(CooccurrenceCountSimilarity.class, id1, id2, false);
  }

  public double similarity(Class<? extends VectorSimilarityMeasure> similarityClass, long id1, long id2,
                           boolean normalize) {
    return similarity(similarityClass, this.getPreferencesFrom(id1, true), id2, normalize);
  }

  public double similarity(Class<? extends VectorSimilarityMeasure> similarityClass, Vector first, long id2,
                           boolean normalize) {
    VectorSimilarityMeasure similarityMeasure = ClassUtils.instantiateAs(similarityClass,
       VectorSimilarityMeasure.class);

    Vector second = this.getPreferencesFrom(id2, true);

    int nonZerosFirst = first.getNumNonZeroElements();
    int nonZerosSecond = second.getNumNonZeroElements();

    if (nonZerosFirst <= nonZerosSecond) {
      return computeSimilarity(similarityMeasure, first, second, normalize);
    } else {
      return computeSimilarity(similarityMeasure, first, second, normalize);
    }
  }

  public void set(long from, long to, double value) {
    if (!idMap.containsKey(from)) {
      ids.add(from);
      idMap.put(from, index++);
    }
    history.setQuick(idMap.get(from), (int) to, value);
  }

  public boolean checkAndSet(long entity, long to, double value, double oldValue) {
    if (get(entity, to) == oldValue) {
      set(entity, to, value);
      return true;
    }
    return false;
  }

  public double get(long from, long to) {
    if (idMap.containsKey(from)) {
      return history.getQuick(idMap.get(from), (int) to);
    }
    return 0;
  }

  public void remove(long entity, long to) {
    if (idMap.containsKey(entity)) {
      history.setQuick(idMap.get(entity), (int) to, 0);
    }
  }

  public boolean checkAndRemove(long entity, long to, double oldValue) {
    if (get(entity, to) == oldValue) {
      remove(entity, to);
      return true;
    }
    return false;
  }


  private double computeSimilarity(VectorSimilarityMeasure similarityMeasure, Vector first, Vector second,
                                   boolean normalize) {
    if (normalize) {
      first = similarityMeasure.normalize(first);
      second = similarityMeasure.normalize(second);
    }

    double normFirst = similarityMeasure.norm(first);
    double normSecond = similarityMeasure.norm(second);

    double dot = 0;

    boolean advance1 = true;
    boolean advance2 = false;

    Iterator<Vector.Element> firstIterator = first.nonZeroes().iterator();
    Iterator<Vector.Element> secondIterator = second.nonZeroes().iterator();
    int i1 = -1;
    int i2 = -1;

    while (firstIterator.hasNext() || secondIterator.hasNext()) {
      if (advance1) {
        i1 = firstIterator.next().index();
      }
      if (advance2) {
        i2 = secondIterator.next().index();
      }
      advance1 = false;
      advance2 = false;

      if (i1 == i2) {
        dot += similarityMeasure.aggregate(first.getQuick(i1), second.getQuick(i2));
      } else if (i1 < i2) {
        advance1 = true;
      } else {
        advance2 = true;
      }
      if(advance1 && !firstIterator.hasNext()) break;
      if(advance2 && !secondIterator.hasNext()) break;
    }
    return similarityMeasure.similarity(dot, normFirst, normSecond, first.getNumNonZeroElements());
  }

  private final class LongArrayListIterator extends AbstractLongPrimitiveIterator {
    private int i = 0;

    @Override
    public long nextLong() {
      return ids.get(i++);
    }

    @Override
    public long peek() {
      return ids.get(i);
    }

    @Override
    public void skip(int n) {
      i += n;
    }

    @Override
    public boolean hasNext() {
      return i < ids.size();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Deleting while iterating not allowed");
    }
  }


  private final class AllIterable implements Iterable<Pair<Long, Vector.Element>> {
    int processedSoFar = -1;
    //int inVectorIndex = 0;
    Vector current = new SequentialAccessSparseVector(1);
    Iterator<Vector.Element> currentItemIterator = current.nonZeroes().iterator();


    @Override
    public Iterator<Pair<Long, Vector.Element>> iterator() {
      return new AllIterator();
    }

    private class AllIterator implements Iterator<Pair<Long, Vector.Element>> {

      @Override
      public boolean hasNext() {
        if(!currentItemIterator.hasNext()){
          if(processedSoFar>=history.numRows()-1){
            return false;
          }
          else{
            current = history.viewRow(++processedSoFar);
            currentItemIterator = current.nonZeroes().iterator();
            return currentItemIterator.hasNext();
          }

        }
        return true;
      }

      @Override
      public Pair<Long, Vector.Element> next() {
        Vector.Element e = currentItemIterator.next();
        long from = ids.get(processedSoFar);
        return Pair.of(from, e);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("Deleting while iterating not allowed");
      }
    }
  }

  private final class MatrixIterable implements Iterable<MatrixSlice> {

    @Override
    public Iterator<MatrixSlice> iterator() {
      return new MatrixIterator(history.iterator());
    }

    private class MatrixIterator implements Iterator<MatrixSlice> {

      Iterator<MatrixSlice> underlyingIterator;

      private MatrixIterator(Iterator<MatrixSlice> underlyingIterator) {
        this.underlyingIterator = underlyingIterator;
      }

      @Override
      public boolean hasNext() {
        return underlyingIterator.hasNext();
      }

      @Override
      public MatrixSlice next() {
        MatrixSlice slice = underlyingIterator.next();
        return new MatrixSlice(slice.vector(), (int) ids.get(slice.index()));
      }

      @Override
      public void remove() {
        iterator().remove();
      }
    }
  }
}
