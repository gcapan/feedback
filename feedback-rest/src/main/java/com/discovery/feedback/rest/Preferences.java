package com.discovery.feedback.rest;

import com.discovery.feedback.model.MatrixBackedDataModel;
import org.apache.mahout.cf.taste.common.TasteException;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("preferences")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
final class Preferences {

  // TODO: All REST endpoints should share the same datamodel.
  //@Inject
  private MatrixBackedDataModel model;

  @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
  private ConnectionFactory connectionFactory;

  @Resource(lookup = "jms/feedbackQueue")
  private Queue queue;

  @GET
  public Float getRatingForItem(@MatrixParam("itemID") long itemID, @MatrixParam("userID") long userID) throws TasteException {
    return model.getPreferenceValue(userID, itemID);
  }

  @POST
  public void setRatingForItem(@MatrixParam("itemID") long itemID, @MatrixParam("userID") long userID, String value) {
    connectionFactory.createContext().createProducer().send(queue, String.format("%s,%s,%s", userID, itemID, value));
  }

  @DELETE
  public void deleteItemRating(@MatrixParam("itemID") long itemID, @MatrixParam("userID") long userID) throws TasteException {
    model.removePreference(userID, itemID);
  }
}
