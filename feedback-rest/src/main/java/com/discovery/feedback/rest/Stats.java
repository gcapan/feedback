package com.discovery.feedback.rest;

import com.discovery.feedback.rest.adapters.SideInfoAwareDataModelBean;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.solr.client.solrj.SolrServerException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("stats")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class Stats {

  // TODO: All REST endpoints should share the same datamodel.
  // @ApplicationScoped
  // @Inject
  private SideInfoAwareDataModelBean model;

  public Stats() throws IOException, SolrServerException {
    model = SideInfoAwareDataModelBean.getInstance();
  }

  @GET
  @Path("users/count")
  public int getNumUsers() throws TasteException {
    return model.getNumUsers();
  }

  @GET
  @Path("items/count")
  public int getNumItems() throws TasteException {
    return model.getNumItems();
  }
}
