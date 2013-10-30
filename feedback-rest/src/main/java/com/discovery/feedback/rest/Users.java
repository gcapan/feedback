package com.discovery.feedback.rest;

import com.discovery.contentdb.matrix.exception.ContentException;
import com.discovery.feedback.model.SideInfoAwareDataModel;
import com.discovery.feedback.rest.adapters.SideInfoAwareDataModelBean;
import org.apache.mahout.cf.taste.common.TasteException;
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

  @GET
  @Path("{userID}")
  public long[] getItemIDsFromUser(@PathParam("userID") long userID) throws TasteException {
    return model.getItemIDsFromUser(userID).toArray();
  }

  @GET
  public long[] getUsers(@QueryParam("contentDimension") String contentDimension,
                         @QueryParam("keyword") String keyword,
                         @QueryParam("latitude") Double latitude,
                         @QueryParam("longitude") Double longitude,
                         @QueryParam("rangeInKm") Integer rangeInKm,
                         @QueryParam("maxResults") Integer maxResults) throws ContentException {
    if(model instanceof SideInfoAwareDataModelBean) {
        SideInfoAwareDataModel sideInfoAwareDataModel = model;
      if(latitude != null && longitude != null && rangeInKm != null) {
        return sideInfoAwareDataModel.getUsers(contentDimension, keyword, latitude, longitude, rangeInKm).toArray();
      } else {
        return sideInfoAwareDataModel.getUsers(contentDimension, keyword, maxResults).toArray();
      }
    } else {
      throw new IllegalArgumentException("An object of " + SideInfoAwareDataModel.class.getCanonicalName() + " is required for this operation.");
    }
  }
}