package com.discovery.feedback.rest;

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

@Path("items")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public final class Items {

  // TODO: All REST endpoints should share the same datamodel.
//  @ApplicationScoped
//  @Inject
  private SideInfoAwareDataModelBean model;

  public Items() throws IOException, SolrServerException {
    model = SideInfoAwareDataModelBean.getInstance();
  }

  // TODO: Where should this be mapped. getItemIDs() and getNumItems() share the same path & method.
  //  @GET
  //  @Path("allIds")
  //  public Id[] getItemIds() {
  //    throw new UnsupportedOperationException("Not Implemented");
  //  }

  @GET
  @Path("{itemId}")
  public Preference[] getUserPreferencesForItem(@PathParam("itemId") long itemId) throws TasteException {
    return Preference.toPreferenceArray(model.getPreferencesForItem(itemId));
  }

  @GET
  @Path("{itemId}/numCommonUsersRated/{secondItemId}")
  public int getNumCommonRatedItems(@PathParam("itemId") long itemId, @PathParam("secondItemId") long secondItemId) throws TasteException {
    return model.getNumUsersRated(itemId, secondItemId);
  }

  @GET
  @Path("{itemId}/similarity/{secondItemId}")
  public double getUserSimilarity(@QueryParam("similarityMeasureClass") String similarityMeasureClass,
                                  @PathParam("itemId") long itemId,
                                  @PathParam("secondItemId") long secondItemId,
                                  @QueryParam("normalize") boolean normalize) throws ClassNotFoundException, TasteException {

    return model.getItemSimilarity((Class<VectorSimilarityMeasure>) Class.forName(similarityMeasureClass), itemId, secondItemId, normalize);
  }

  @GET
  public Id[] getItems(@QueryParam("contentDimension") String contentDimension,
                       @QueryParam("keyword") String keyword,
                       @QueryParam("latitude") Double latitude,
                       @QueryParam("longitude") Double longitude,
                       @QueryParam("rangeInKm") Integer rangeInKm,
                       @QueryParam("maxResults") Integer maxResults) throws Exception {
    if (model instanceof SideInfoAwareDataModelBean) {
      SideInfoAwareDataModel sideInfoAwareDataModel = model;
      if (latitude != null && longitude != null && rangeInKm != null) {

        return Id.toIdArray(sideInfoAwareDataModel.getItems(contentDimension, keyword, latitude, longitude, rangeInKm));
      } else {
        return Id.toIdArray(sideInfoAwareDataModel.getItems(contentDimension, keyword, maxResults));
      }
    } else {
      throw new IllegalArgumentException("An object of " + SideInfoAwareDataModel.class.getCanonicalName() + " is required for this operation.");
    }
  }

  @GET
  public int getNumItems() throws TasteException {
    return model.getNumItems();
  }

  @GET
  @Path("{itemId}/numUsersWithPreferenceFor")
  public int getNumUsersWithPreferenceFor(@PathParam("itemId") long itemId) throws TasteException {
    return model.getNumUsersWithPreferenceFor(itemId);
  }
}