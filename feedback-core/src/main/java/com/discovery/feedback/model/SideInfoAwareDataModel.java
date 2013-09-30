package com.discovery.feedback.model;

import com.discovery.contentdb.matrix.Content;
import com.discovery.contentdb.matrix.exception.ContentException;
import com.discovery.feedback.model.history.History;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.math.Vector;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.Collection;

/**
 * {@author} gcapan
 */
public class SideInfoAwareDataModel extends MatrixBackedDataModel {
  Content userSideInfo;
  Content itemSideInfo;


  public SideInfoAwareDataModel(History userHistory, History itemHistory, Content userSideInfo,
                                Content itemSideInfo) {
    super(userHistory, itemHistory);
    this.userSideInfo = userSideInfo;
    this.itemSideInfo = itemSideInfo;
  }

  public void addUser (long user, Vector demographics) {
    userSideInfo.assignRow((int)user, demographics);
  }

  public void addItem (long item, Vector content) {
    itemSideInfo.assignRow((int) item, content);
  }

  public FastIDSet getUsers(String contentDimension, String keyword, int maxResults) throws ContentException{
    return userSideInfo.getCandidates(contentDimension, keyword, maxResults);
  }
  public FastIDSet getUsers(String contentDimension, SolrQuery solrQuery, int maxResults) throws ContentException{
    return userSideInfo.getCandidates(contentDimension, solrQuery, maxResults);
  }

  public FastIDSet getUsers(String contentDimension, String keyword, double latitude,
                            double longitude, int rangeInKm) throws ContentException{
    return userSideInfo.getCandidates(contentDimension, keyword, latitude, longitude, rangeInKm);
  }

  public FastIDSet getItems(String contentDimension, String keyword, int maxResults) throws ContentException{
    return itemSideInfo.getCandidates(contentDimension, keyword, maxResults);
  }

  public FastIDSet getItems(String contentDimension, SolrQuery solrQuery, int maxResults) throws ContentException{
    return itemSideInfo.getCandidates(contentDimension, solrQuery, maxResults);
  }

  public FastIDSet getItems(String contentDimension, String keyword, double latitude,
                            double longitude, int rangeInKm) throws ContentException{
    return itemSideInfo.getCandidates(contentDimension, keyword, latitude, longitude, rangeInKm);
  }

  public Vector getUserVector(long user) {
    return userSideInfo.viewRow((int) user);
  }

  public double getQuickFromUsers(long user, int column){
    return userSideInfo.getQuick((int) user, column);
  }

  /**
   * @param user The user id
   * @param label Here if the field of interest is text or multinomial, label should be the word/category of interest.
   *              If the field is boolean or numerical, label should be the field name
   */
  public double getQuickFromUsers(long user, String label ){
    return userSideInfo.getQuick((int) user, userSideInfo.getColumnLabelBindings().get(label));
  }

  public Vector getItemVector(long item) {
    return itemSideInfo.viewRow((int) item);
  }

  public double getQuickFromItems(long item, int column) {
    return itemSideInfo.getQuick((int) item, column);
  }

  /**
   * @param item The item id
   * @param label Here if the field of interest is text or multinomial, label should be the word/category of interest.
   *              If the field is boolean or numerical, label should be the field name
   */
  public double getQuickFromItems(long item, String label) {
    return itemSideInfo.getQuick((int) item, itemSideInfo.getColumnLabelBindings().get(label));
  }
}
