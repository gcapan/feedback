package com.discovery.feedback.model;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.AbstractLongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.AbstractDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseRowMatrix;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.list.LongArrayList;
import org.apache.mahout.math.map.OpenLongIntHashMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class MatrixBackedDataModel extends AbstractDataModel {
  //for fast access to a user history
  private Matrix userHistory;

  //for fast access to an item history
  private Matrix itemHistory;
  private boolean hasPreferenceValue = false;
  private OpenLongIntHashMap usersMap;
  private OpenLongIntHashMap itemsMap;
  private LongArrayList[] ids;
  private int userIndex = 0;
  private int itemIndex = 0;


  public MatrixBackedDataModel(int maxNoOfUsers, int maxNoOfItems, int maxItemId, int maxUserId) {
    ids = new LongArrayList[]{new LongArrayList(maxNoOfUsers), new LongArrayList(maxNoOfItems)};
    userHistory = new SparseRowMatrix(maxNoOfUsers, maxItemId, true);
    itemHistory = new SparseRowMatrix(maxNoOfItems, maxUserId, true);
    usersMap = new OpenLongIntHashMap(maxNoOfUsers);
    itemsMap = new OpenLongIntHashMap(maxNoOfItems);
  }


  //This is further going to be used to create item cooccurrence matrix
  public void persist(Configuration configuration, Path path) throws IOException {
    SequenceFile.Writer writer = SequenceFile.createWriter(FileSystem.get(configuration), configuration, path,
      LongWritable.class, VectorWritable.class);
    for(long userId:ids[0].elements()){
      writer.append(userId, new VectorWritable(userHistory.viewRow(usersMap.get(userId))));
    }
    writer.close();
  }

  //Call that to iterate over all preferences such that:
  //for all items{
  //    for all users preferred this item{
  //       ...
  public Iterable<Preference> preferences() throws TasteException {
    return new AllIterable();
  }

  @Override
  public LongPrimitiveIterator getUserIDs() throws TasteException {
    return new LongArrayListIterator(0);
  }


  @Override
  public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
    Vector preferencesVector = userHistory.viewRow(usersMap.get(userID));
    PreferenceArray preferenceArray = new GenericUserPreferenceArray(preferencesVector.getNumNonZeroElements());
    preferenceArray.setUserID(0, userID);

    int i = 0;
    for (Vector.Element e : preferencesVector.nonZeroes()) {
      preferenceArray.setItemID(i, e.index());
      preferenceArray.setValue(i, (float) e.get());
      i++;
    }
    return preferenceArray;
  }

  @Override
  public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
    Vector preferencesVector = userHistory.viewRow(usersMap.get(userID));
    FastIDSet items = new FastIDSet(preferencesVector.getNumNonZeroElements());

    for (Vector.Element e : preferencesVector.nonZeroes()) {
      items.add(ids[1].get(e.index()));
    }
    return items;
  }

  @Override
  public LongPrimitiveIterator getItemIDs() throws TasteException {
    return new LongArrayListIterator(1);
  }

  @Override
  public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
    Vector preferencesVector = itemHistory.viewRow(itemsMap.get(itemID));
    PreferenceArray preferenceArray = new GenericItemPreferenceArray(preferencesVector.getNumNonZeroElements());
    preferenceArray.setItemID(0, itemID);

    int i = 0;
    for (Vector.Element e : preferencesVector.nonZeroes()) {
      preferenceArray.setUserID(i, e.index());
      preferenceArray.setValue(i, (float) e.get());
      i++;
    }
    return preferenceArray;

  }

  @Override
  public Float getPreferenceValue(long userID, long itemID) throws TasteException {
    return (float) userHistory.getQuick(usersMap.get(userID), (int)itemID);
  }

  @Override
  public Long getPreferenceTime(long userID, long itemID) throws TasteException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getNumItems() throws TasteException {
    return ids[1].size();
  }

  @Override
  public int getNumUsers() throws TasteException {
    return ids[0].size();
  }

  @Override
  public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
    return itemHistory.viewRow(itemsMap.get(itemID)).getNumNonZeroElements();
  }

  @Override
  public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
    Vector i1 = itemHistory.viewRow(itemsMap.get(itemID1));
    Vector i2 = itemHistory.viewRow(itemsMap.get(itemID2));

    Iterator<Vector.Element> i1Iterator = i1.nonZeroes().iterator();
    Iterator<Vector.Element> i2Iterator = i2.nonZeroes().iterator();
    int count = 0;
    int u1 = -1;
    int u2 = -1;
    boolean advance1 = true;
    boolean advance2 = false;

    while (i1Iterator.hasNext() && i2Iterator.hasNext()) {
      if (advance1) {
        u1 = i1Iterator.next().index();
      }
      if (advance2) {
        u2 = i2Iterator.next().index();
      }
      advance1 = false;
      advance2 = false;

      if (u1 == u2) {
        count++;
      } else if (u1 < u2) {
        advance1 = true;
      } else {
        advance2 = true;
      }
    }
    return count;
  }

  @Override
  //This should be atomic
  public void setPreference(long userID, long itemID, float value) throws TasteException {
    hasPreferenceValue = true;
    LongArrayList users = ids[0];
    LongArrayList items = ids[1];
    if (!users.contains(userID)) {
      ids[0].add(userID);
      usersMap.put(userID, userIndex++);
    }
    if (!items.contains(itemID)) {
      ids[1].add(itemID);
      itemsMap.put(itemID, itemIndex++);
    }
    userHistory.setQuick(usersMap.get(userID), (int)itemID, value);
    itemHistory.setQuick(itemsMap.get(itemID), (int)userID, value);
  }

  @Override
  //This should be atomic
  public void removePreference(long userID, long itemID) throws TasteException {
    if (!usersMap.containsKey(userID)) {
      throw new NoSuchUserException();
    }
    if (!itemsMap.containsKey(itemID)) {
      throw new NoSuchItemException();
    }
    userHistory.setQuick(usersMap.get(userID), (int)itemID, 0);
    itemHistory.setQuick(itemsMap.get(itemID), (int)userID, 0);
  }

  @Override
  public boolean hasPreferenceValues() {
    return this.hasPreferenceValue;
  }

  @Override
  public void refresh(Collection<Refreshable> alreadyRefreshed) {
  }

  private final class LongArrayListIterator extends AbstractLongPrimitiveIterator {
    private int index = 0;
    private int i;

    LongArrayListIterator(int i) {
      this.i = i;
    }

    @Override
    public long nextLong() {
      return ids[i].get(index++);
    }

    @Override
    public long peek() {
      return ids[i].get(index);
    }

    @Override
    public void skip(int n) {
      index += n;
    }

    @Override
    public boolean hasNext() {
      return index < ids[i].size();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class AllIterable implements Iterable<Preference> {
    int index = 0;
    int index2 = 0;
    PreferenceArray currentItemPreferencesArray = new GenericItemPreferenceArray(0);

    @Override
    public Iterator<Preference> iterator() {
      return new AllIterator();
    }

    private class AllIterator implements Iterator<Preference> {
      @Override
      public boolean hasNext() {
        if (index2 < currentItemPreferencesArray.length()) {
          return true;
        }
        return index < ids[1].size();
      }

      @Override
      public Preference next() {
        try {
          if (!(index2 < currentItemPreferencesArray.length())) {
            long item = ids[1].get(index++);
            index2 = 0;
            currentItemPreferencesArray = getPreferencesForItem(item);
          }
          return currentItemPreferencesArray.get(index2++);

        } catch (TasteException te) {
          throw new RuntimeException(te);
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }


    }
  }
}
