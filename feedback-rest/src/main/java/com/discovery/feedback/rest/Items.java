package com.discovery.feedback.rest;

import com.discovery.feedback.rest.adapters.MatrixBackedDataModelBean;
import com.discovery.feedback.rest.adapters.Preference;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;

@Path("items")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class Items {

  // TODO: All REST endpoints should share the same datamodel and recommender.
  @EJB
  private MatrixBackedDataModelBean model;
  // TODO: Move recommender to another rest endpoint.
  //  private ItemBasedRecommender recommender = new BogusRecommender();

  @GET
  public Integer getNumItems() throws TasteException {
    return model.getNumItems();
  }

//  @GET
//  @Path("{itemID}/similars")
//  public List<RecommendedItem> getMostSimilarItems(@PathParam("itemID") long itemID, @DefaultValue("10") @QueryParam("n") int n) throws TasteException {
//    return recommender.mostSimilarItems(itemID, n);
//  }

  @GET
  @Path("{itemID}")
  public Preference[] getUserPreferencesForItem(@PathParam("itemID") long itemID) throws TasteException {

    PreferenceArray preferencesForItem = model.getPreferencesForItem(itemID);
    Preference[] prefArr = new Preference[0];

    if (preferencesForItem != null) {
      prefArr = new Preference[preferencesForItem.length()];
      Iterator<org.apache.mahout.cf.taste.model.Preference> iterator = preferencesForItem.iterator();
      int i = 0;
      for (org.apache.mahout.cf.taste.model.Preference preference : preferencesForItem) {
        prefArr[i] = new Preference(preference.getUserID(), preference.getItemID(), preference.getValue());
        i++;
      }
    }
    return prefArr;
  }

  @GET
  @Path("{itemID}/{userID}")
  public Float getRatingForItem(@PathParam("itemID") long itemID, @PathParam("userID") long userID) throws TasteException {
    return model.getPreferenceValue(userID, itemID);
  }

  @POST
  @Path("{itemID}/{userID}")
  public void setRatingForItem(@PathParam("itemID") long itemID, @PathParam("userID") long userID, String value) throws TasteException {
    model.setPreference(userID, itemID, Float.parseFloat(value));
  }

  @DELETE
  @Path("{itemID}/{userID}")
  public void deleteItemRating(@PathParam("itemID") long itemID, @PathParam("userID") long userID) throws TasteException {
    model.removePreference(userID, itemID);
  }
}