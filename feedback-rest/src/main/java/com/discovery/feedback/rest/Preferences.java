package com.discovery.feedback.rest;

import com.discovery.feedback.rest.adapters.SideInfoAwareDataModelBean;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.solr.client.solrj.SolrServerException;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@JMSDestinationDefinition(
   name = "java:comp/jms/feedbackQueue",
   interfaceName = "javax.jms.Queue",
   destinationName = "PhysicalFeedbackQueue")
@Named
@Path("preferences")
@RequestScoped
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class Preferences {

  @Inject
  private JMSContext context;

  @Resource(lookup = "jms/feedbackQueue")
  private Queue queue;

  private JMSProducer producer;

  // TODO: All REST endpoints should share the same datamodel.
  //@Inject
  private SideInfoAwareDataModelBean model;

  public Preferences() throws IOException, SolrServerException {
    model = SideInfoAwareDataModelBean.getInstance();
  }

  @GET
  public Float getPreference(@MatrixParam("itemId") long itemId, @MatrixParam("userId") long userId) throws TasteException {
    return model.getPreferenceValue(userId, itemId);
  }

  @POST
  public void setPreference(@MatrixParam("itemId") long itemId, @MatrixParam("userId") long userId, String value) {
    context.createProducer().send(queue, String.format("%s,%s,%s", userId, itemId, value));
  }

  @DELETE
  public void deletePreference(@MatrixParam("itemId") long itemId, @MatrixParam("userId") long userId) throws TasteException {
    model.removePreference(userId, itemId);
  }
}
