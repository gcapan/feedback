package com.discovery.feedback.model;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.math.Vector;

import java.util.Collection;

/**
 * {@author} gcapan
 */
public class SideInfoAwareDataModel extends MatrixBackedDataModel {

  public DataModel dataModel;
  public ContentTable contentTable;
  public DemographicsTable demographicsTable;
  public final int MAX = 100;

  public SideInfoAwareDataModel(int maxNoOfUsers, int maxNoOfItems, int maxUserId, int maxItemId) {
    super(maxNoOfUsers, maxNoOfItems, maxUserId, maxItemId);    //To change body of overridden methods use File | Settings |
    // File Templates.
  }

  public SideInfoAwareDataModel(int maxNoOfUsers, int maxNoOfItems, int maxUserId, int maxItemId, DataModel base,
                                ContentTable contentTable, DemographicsTable demographicsTable) {
    this(maxNoOfUsers, maxNoOfItems, maxUserId, maxItemId);
    this.dataModel = base;
    this.contentTable = contentTable;
    this.demographicsTable = demographicsTable;
  }



  public void addUser(long user, Vector demographics){
    demographicsTable.add(user, demographics);
  }
  public void addItem(long item, Vector content){
    contentTable.add(item, content);
  }


  public LongPrimitiveIterator getUserIDs(Vector matches) throws TasteException{
    return demographicsTable.order(getUserIDs(), matches, MAX);
  }
  public LongPrimitiveIterator getItemIDs(Vector matches) throws TasteException {
    return contentTable.order(getItemIDs(), matches, MAX);
  }

  public FastIDSet getItemIDsFromUser(long user, Vector itemMatches)throws TasteException {
    return contentTable.order(getItemIDsFromUser(user),itemMatches);
  }

  public void refresh(Collection<Refreshable> alreadyRefreshed) {
    dataModel.refresh(alreadyRefreshed);
  }

  public LongPrimitiveIterator getUserIDs() throws TasteException {
    return dataModel.getUserIDs();
  }

  public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
    return dataModel.getPreferencesFromUser(userID);
  }

  public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
    return dataModel.getItemIDsFromUser(userID);
  }

  public LongPrimitiveIterator getItemIDs() throws TasteException {
    return dataModel.getItemIDs();
  }

  public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
    return dataModel.getPreferencesForItem(itemID);
  }

  public Float getPreferenceValue(long userID, long itemID) throws TasteException {
    return dataModel.getPreferenceValue(userID, itemID);
  }

  public Long getPreferenceTime(long userID, long itemID) throws TasteException {
    return dataModel.getPreferenceTime(userID, itemID);
  }

  public int getNumItems() throws TasteException {
    return dataModel.getNumItems();
  }

  public int getNumUsers() throws TasteException {
    return dataModel.getNumUsers();
  }

  public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
    return dataModel.getNumUsersWithPreferenceFor(itemID);
  }

  public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
    return dataModel.getNumUsersWithPreferenceFor(itemID1, itemID2);
  }

  public void setPreference(long userID, long itemID, float value) throws TasteException {
    dataModel.setPreference(userID, itemID, value);
  }

  public void removePreference(long userID, long itemID) throws TasteException {
    dataModel.removePreference(userID, itemID);
  }

  public boolean hasPreferenceValues() {
    return dataModel.hasPreferenceValues();
  }

  public float getMaxPreference() {
    return dataModel.getMaxPreference();
  }

  public float getMinPreference() {
    return dataModel.getMinPreference();
  }

}
