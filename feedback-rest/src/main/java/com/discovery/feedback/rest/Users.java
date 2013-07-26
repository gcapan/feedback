package com.discovery.feedback.rest;

import com.discovery.feedback.rest.adapters.MatrixBackedDataModelBean;
import org.apache.mahout.cf.taste.common.TasteException;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("users")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class Users {

  // TODO: All REST endpoints should share the same datamodel and recommender.
  @EJB
  private MatrixBackedDataModelBean model;

  // TODO: Move recommender to another rest endpoint.
  //  private UserBasedRecommender recommender;

  @GET
  public Integer getNumUsers() throws TasteException {
    return model.getNumUsers();
  }

//  @GET
//  @Path("{userID}/similars")
//  public Long[] getSimilarUsers(@PathParam("userID") long userID, @DefaultValue("10") @QueryParam("n") int n) throws TasteException {
//    return ArrayUtils.toObject(recommender.mostSimilarUserIDs(userID, n));
//  }
//
//  @GET
//  @Path("{userID}/topN")
//  public List<RecommendedItem> getTopN(@PathParam("userID") long userID, @DefaultValue("10") @QueryParam("n") int n) throws TasteException {
//    return recommender.recommend(userID, n);
//  }
//
//  @GET
//  @Path("{userID}/predict/{itemID}")
//  public Float predictRatingForItem(@PathParam("userID") long userID, @PathParam("itemID") long itemID) throws TasteException {
//    return recommender.estimatePreference(userID, itemID);
//  }

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