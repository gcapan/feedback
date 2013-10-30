package com.discovery.feedback.rest;

import com.discovery.feedback.model.SideInfoAwareDataModel;
import com.discovery.feedback.rest.adapters.Id;
import com.discovery.feedback.rest.adapters.Preference;
import com.discovery.feedback.rest.adapters.SideInfoAwareDataModelBean;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.PreferenceArray;
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
//
//  @POST
//  @Path("by_solr_query")
//  public long[] getItemsBySolrQuery() throws ContentException {
//    if (model instanceof SideInfoAwareDataModel) {
//      SideInfoAwareDataModel sideInfoAwareDataModel = (SideInfoAwareDataModel) model;
//      return sideInfoAwareDataModel.getItems(contentDimension, , maxResults).toArray();
//    } else {
//      throw new IllegalArgumentException("An object of " + SideInfoAwareDataModel.class.getCanonicalName() + " is required for this operation.");
//    }
//  }
}