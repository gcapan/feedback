package com.discovery.feedback.rest;

import com.discovery.feedback.model.MatrixBackedDataModel;
import org.apache.mahout.cf.taste.common.TasteException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("preferences")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class Preferences {

  // TODO: All REST endpoints should share the same datamodel.
  @Inject
  private MatrixBackedDataModel model;

  @GET
  public Float getRatingForItem(@MatrixParam("itemID") long itemID, @MatrixParam("userID") long userID) throws TasteException {
    return model.getPreferenceValue(userID, itemID);
  }

  @POST
  public void setRatingForItem(@MatrixParam("itemID") long itemID, @MatrixParam("userID") long userID, String value) throws TasteException {
    model.setPreference(userID, itemID, Float.parseFloat(value));
  }

  @DELETE
  public void deleteItemRating(@MatrixParam("itemID") long itemID, @MatrixParam("userID") long userID) throws TasteException {
    model.removePreference(userID, itemID);
  }
}
