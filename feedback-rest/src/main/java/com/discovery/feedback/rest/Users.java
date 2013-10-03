package com.discovery.feedback.rest;

import com.discovery.contentdb.matrix.exception.ContentException;
import com.discovery.feedback.model.MatrixBackedDataModel;
import com.discovery.feedback.model.SideInfoAwareDataModel;
import org.apache.mahout.cf.taste.common.TasteException;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("users")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class Users {

  // TODO: All REST endpoints should share the same datamodel.
  @EJB
  private MatrixBackedDataModel model;

  @GET
  @Path("{userID}")
  public long[] getItemIDsFromUser(@PathParam("userID") long userID) throws TasteException {
    return model.getItemIDsFromUser(userID).toArray();
  }

  @GET
  @Path("{userID}/{itemID}")
  public Float getRatingForItem(@PathParam("itemID") long itemID, @PathParam("userID") long userID) throws TasteException {
    return model.getPreferenceValue(userID, itemID);
  }

  @GET
  public long[] getUsers(@QueryParam("contentDimension") String contentDimension,
                         @QueryParam("keyword") String keyword,
                         @QueryParam("latitude") Double latitude,
                         @QueryParam("longitude") Double longitude,
                         @QueryParam("rangeInKm") Integer rangeInKm,
                         @QueryParam("maxResults") Integer maxResults) throws ContentException {
    if(model instanceof SideInfoAwareDataModel) {
        SideInfoAwareDataModel sideInfoAwareDataModel = (SideInfoAwareDataModel) model;
      if(latitude != null && longitude != null && rangeInKm != null) {
        return sideInfoAwareDataModel.getUsers(contentDimension, keyword, latitude, longitude, rangeInKm).toArray();
      } else {
        return sideInfoAwareDataModel.getUsers(contentDimension, keyword, maxResults).toArray();
      }
    } else {
      throw new IllegalArgumentException("An object of " + SideInfoAwareDataModel.class.getCanonicalName() + " is required for this operation.");
    }
  }

  @POST
  @Path("{userID}/{itemID}")
  public void setRatingForItem(@PathParam("itemID") long itemID, @PathParam("userID") long userID, String value) throws TasteException {
    model.setPreference(userID, itemID, Float.parseFloat(value));
  }

  @DELETE
  @Path("{userID}/{itemID}")
  public void deleteItemRating(@PathParam("itemID") long itemID, @PathParam("userID") long userID) throws TasteException {
    model.removePreference(userID, itemID);
  }
}