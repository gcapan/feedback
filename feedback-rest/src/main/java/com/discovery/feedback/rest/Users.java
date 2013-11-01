package com.discovery.feedback.rest;

import com.discovery.contentdb.matrix.exception.ContentException;
import com.discovery.feedback.model.SideInfoAwareDataModel;
import com.discovery.feedback.rest.adapters.Id;
import com.discovery.feedback.rest.adapters.Preference;
import com.discovery.feedback.rest.adapters.SideInfoAwareDataModelBean;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.VectorSimilarityMeasure;
import org.apache.solr.client.solrj.SolrServerException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("users")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public final class Users {

  // TODO: All REST endpoints should share the same datamodel.
  //  @ApplicationScoped
  //  @Inject
  private SideInfoAwareDataModelBean model;

  public Users() throws IOException, SolrServerException {
    model = SideInfoAwareDataModelBean.getInstance();
  }

  // TODO: Implement pagination (over a forward iterator?)
  @GET
  public Id[] getUserIds() {
    throw new UnsupportedOperationException("Not Implemented");
  }

  @GET
  @Path("{userId}/itemIds")
  public Id[] getItemIDsFromUser(@PathParam("userId") long userId) throws TasteException {
    return Id.toIdArray(model.getItemIDsFromUser(userId));
  }

  @GET
  @Path("{userId}/numCommonRatedItems/{secondUserId}")
  public int getNumCommonRatedItems(@PathParam("userId") long userId, @PathParam("secondUserId") long secondUserId) throws TasteException {
    return model.getNumItemsRatedBy(userId, secondUserId);
  }

  @GET
  @Path("{userId}/similarity/{secondUserId}")
  public double getUserSimilarity(@QueryParam("similarityMeasureClass") String similarityMeasureClass,
                                  @PathParam("userId") long userId,
                                  @PathParam("secondUserId") long secondUserId,
                                  @QueryParam("normalize") boolean normalize) throws ClassNotFoundException, TasteException {

    return model.getUserSimilarity((Class<VectorSimilarityMeasure>) Class.forName(similarityMeasureClass), userId, secondUserId, normalize);
  }

  @GET
  @Path("{userId}/preferences")
  public Preference[] getPreferencesFromUser(@PathParam("userId") long userId) throws TasteException {
    return Preference.toPreferenceArray(model.getPreferencesFromUser(userId));
  }

  @GET
  @Path("bySideInfo")
  public Id[] getUsers(@QueryParam("contentDimension") String contentDimension,
                       @QueryParam("keyword") String keyword,
                       @QueryParam("latitude") Double latitude,
                       @QueryParam("longitude") Double longitude,
                       @QueryParam("rangeInKm") Integer rangeInKm,
                       @QueryParam("maxResults") @DefaultValue("10") Integer maxResults) throws ContentException {

    if (model instanceof SideInfoAwareDataModelBean) {
      SideInfoAwareDataModel sideInfoAwareDataModel = model;
      if (latitude != null && longitude != null && rangeInKm != null) {
        return Id.toIdArray(sideInfoAwareDataModel.getUsers(contentDimension, keyword, latitude, longitude, rangeInKm));
      } else {
        return Id.toIdArray(sideInfoAwareDataModel.getUsers(contentDimension, keyword, maxResults));
      }
    } else {
      throw new IllegalArgumentException("An object of " + SideInfoAwareDataModel.class.getCanonicalName() + " is required for this operation.");
    }
  }

  @GET
  @Path("{userId}/numItemsWithPreferenceFrom")
  public int getNumItemsWithPreferenceFrom(@PathParam("userId") long userId) throws TasteException {
    return model.getNumItemsWithPreferenceFrom(userId);
  }

}