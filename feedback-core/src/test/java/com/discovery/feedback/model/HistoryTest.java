package com.discovery.feedback.model;

import com.discovery.feedback.model.history.History;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.common.Pair;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CosineSimilarity;
import org.apache.mahout.math.list.DoubleArrayList;
import org.apache.mahout.math.list.LongArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@author} gcapan
 */
public class HistoryTest {
  private History history;

  @Before
  public void setup() {
    history = new History(10, 5);
    history.set(2, 3, 4.5);
    history.set(1, 2, 3.4);
    history.set(2, 1, 7.4);
    history.set(3, 4, 5.6);
    history.set(2, 4, 4.6);
  }

  @Test
  public void testGet() {
    assertEquals(3.4, history.get(1, 2), 0);
    assertEquals(4.5, history.get(2, 3), 0);
    assertEquals(5.6, history.get(3, 4), 0);
    assertEquals(0, history.get(3, 2), 0);
  }

  @Test
  public void testGetPreferencesFrom() {
    Vector v = history.getPreferencesFrom(2, false);
    assertEquals(4.5, v.get(3), 0);
    assertEquals(4.6, v.get(4), 0);
    assertEquals(0, v.get(2), 0);
  }

  @Test
  public void testGetCommons() {
    assertEquals(1, history.getCommons(2, 3));
    assertEquals(0, history.getCommons(1, 3));
    assertEquals(0, history.getCommons(1, 2));
  }

  @Test
  public void testSortByValue() {
    LongArrayList ids = new LongArrayList();
    DoubleArrayList values = new DoubleArrayList();
    history.sortByValue(2, ids, values);
    assertEquals(3, ids.get(0));
    assertEquals(4.5, values.get(0), 0);
    assertEquals(4, ids.get(1));
    assertEquals(4.6, values.get(1), 0);
    assertEquals(1, ids.get(2));
    assertEquals(7.4, values.get(2), 0);
  }

  @Test
  public void testSimilarity() {
    double similarity = history.similarity(CosineSimilarity.class, 2, 3, false);
    assertEquals(history.getPreferencesFrom(2, false).dot(history.getPreferencesFrom(3, false)), similarity, 0);
  }

  @Test
  public void testAllFroms() {
    LongPrimitiveIterator it = history.allFroms();
    LongArrayList list = new LongArrayList();
    while (it.hasNext()) {
      list.add(it.next());
    }
    assertTrue(list.size() == 3);
    assertTrue(list.contains(1));
    assertTrue(list.contains(2));
    assertTrue(list.contains(3));
  }


  @Test
  public void testAllPreferences() {
    Iterable<Pair<Long, Vector.Element>> iterable = history.allPreferences();
    Multimap<Long, Pair<Long, Double>> preferences = ArrayListMultimap.create();

    for (Pair<Long, Vector.Element> pref : iterable) {
      preferences.put(pref.getFirst(), Pair.of((long) pref.getSecond().index(), pref.getSecond().get()));
    }

    assertTrue(preferences.containsKey(1l));
    assertTrue(preferences.containsKey(2l));
    assertTrue(preferences.containsKey(3l));
    assertTrue(!preferences.containsKey(4l));
    assertTrue(!preferences.containsKey(0l));

    assertTrue(preferences.get(1l).size() == 1);
    assertTrue(preferences.get(2l).size() == 3);
    assertTrue(preferences.get(3l).size() == 1);

    assertTrue(preferences.get(1l).contains(Pair.of(2l, 3.4)));
    assertTrue(preferences.get(2l).contains(Pair.of(1l, 7.4)));
    assertTrue(preferences.get(2l).contains(Pair.of(3l, 4.5)));
    assertTrue(preferences.get(2l).contains(Pair.of(4l, 4.6)));
    assertTrue(preferences.get(3l).contains(Pair.of(4l, 5.6)));
  }

}


