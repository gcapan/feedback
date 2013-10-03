package com.discovery.feedback.rest;

import com.discovery.contentdb.matrix.exception.ContentException;
import com.discovery.feedback.model.MatrixBackedDataModel;
import com.discovery.feedback.model.SideInfoAwareDataModel;
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

  // TODO: All REST endpoints should share the same datamodel.
  @EJB
  private MatrixBackedDataModel model;

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

  @GET
  public long[] getItems(@QueryParam("contentDimension") String contentDimension,
                         @QueryParam("keyword") String keyword,
                         @QueryParam("latitude") Double latitude,
                         @QueryParam("longitude") Double longitude,
                         @QueryParam("rangeInKm") Integer rangeInKm,
                         @QueryParam("maxResults") Integer maxResults) throws ContentException {
    if(model instanceof SideInfoAwareDataModel) {
      SideInfoAwareDataModel sideInfoAwareDataModel = (SideInfoAwareDataModel) model;
      if(latitude != null && longitude != null && rangeInKm != null) {
        return sideInfoAwareDataModel.getItems(contentDimension, keyword, latitude, longitude, rangeInKm).toArray();
      } else {
        return sideInfoAwareDataModel.getItems(contentDimension, keyword, maxResults).toArray();
      }
    } else {
      throw new IllegalArgumentException("An object of " + SideInfoAwareDataModel.class.getCanonicalName() + " is required for this operation.");
    }
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