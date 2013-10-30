package com.discovery.feedback.rest;

import com.discovery.contentdb.matrix.exception.ContentException;
import com.discovery.feedback.model.MatrixBackedDataModel;
import com.discovery.feedback.model.SideInfoAwareDataModel;
import com.discovery.feedback.rest.adapters.Preference;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("items")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public final class Items {

  // TODO: All REST endpoints should share the same datamodel.
  @Inject
  private MatrixBackedDataModel model;

  @GET
  @Path("{itemId}")
  public Preference[] getUserPreferencesForItem(@PathParam("itemId") long itemId) throws TasteException {

    PreferenceArray preferencesForItem = model.getPreferencesForItem(itemId);
    Preference[] prefArr = new Preference[0];

    if (preferencesForItem != null) {
      prefArr = new Preference[preferencesForItem.length()];
      int i = 0;
      for (org.apache.mahout.cf.taste.model.Preference preference : preferencesForItem) {
        prefArr[i] = new Preference(preference.getUserID(), preference.getItemID(), preference.getValue());
        i++;
      }
    }
    return prefArr;
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
}