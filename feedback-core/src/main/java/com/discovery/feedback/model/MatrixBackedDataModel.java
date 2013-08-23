package com.discovery.feedback.model;

import com.discovery.feedback.model.history.History;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.AbstractLongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.AbstractDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.Pair;
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
  private History userHistory;
  //for fast access to an item history
  private History itemHistory;


  public MatrixBackedDataModel(History userHistory, History itemHistory) {
    this.userHistory = userHistory;
    this.itemHistory = itemHistory;
  }


  public Iterable<Preference> preferences() throws TasteException {
    return new AllIterable();
  }

  @Override
  public LongPrimitiveIterator getUserIDs() throws TasteException {
    return userHistory.allIds();
  }


  @Override
  public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
    Vector preferencesVector = userHistory.getPreferencesFor(userID, false);
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
    return userHistory.getIdsFor(userID);
  }

  @Override
  public LongPrimitiveIterator getItemIDs() throws TasteException {
    return itemHistory.allIds();
  }

  @Override
  public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
    Vector preferencesVector = itemHistory.getPreferencesFor(itemID, false);
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
    return (float) userHistory.get(userID, itemID);
  }

  @Override
  public Long getPreferenceTime(long userID, long itemID) throws TasteException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getNumItems() throws TasteException {
    return itemHistory.getNumEntities();
  }

  @Override
  public int getNumUsers() throws TasteException {
    return userHistory.getNumEntities();
  }

  @Override
  public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
    return itemHistory.getPreferencesFor(itemID, false).getNumNonZeroElements();
  }

  @Override
  public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
    return itemHistory.getCommons(itemID1, itemID2);
  }

  public int getNumItemsWithPreferenceFrom(long userID1, long userID2) throws TasteException {
    return userHistory.getCommons(userID1, userID2);
  }

  @Override
  public void setPreference(long userID, long itemID, float value) throws TasteException {
    throw new UnsupportedOperationException("Do this for individual history matrices");
  }

  @Override
  public void removePreference(long userID, long itemID) throws TasteException {
    throw new UnsupportedOperationException("Do this for individual history matrices");
  }

  @Override
  public boolean hasPreferenceValues() {
    return true;
  }

  @Override
  public void refresh(Collection<Refreshable> alreadyRefreshed) {
  }


  private class AllIterable implements Iterable<Preference> {
    @Override
    public Iterator<Preference> iterator() {
      return new AllIterator(itemHistory.allPreferences().iterator());
    }

    private class AllIterator implements Iterator<Preference> {

      private final Iterator<Pair<Long, Vector.Element>> underlyingIterator;

      public AllIterator(Iterator<Pair<Long, Vector.Element>> iterator) {
        this.underlyingIterator = iterator;
        //To change body of created methods use File | Settings | File Templates.
      }

      @Override
      public boolean hasNext() {
        return underlyingIterator.hasNext();
      }

      @Override
      public Preference next() {
        Pair<Long, Vector.Element> next = underlyingIterator.next();
        return new GenericPreference(next.getSecond().index(), next.getFirst(), (float) next.getSecond().get());
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }


    }
  }
}
