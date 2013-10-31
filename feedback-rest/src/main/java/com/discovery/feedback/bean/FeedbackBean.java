package com.discovery.feedback.bean;

import com.discovery.feedback.rest.adapters.SideInfoAwareDataModelBean;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.solr.client.solrj.SolrServerException;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
   @ActivationConfigProperty(propertyName = "destinationLookup",
      propertyValue = "jms/feedbackQueue"),
   @ActivationConfigProperty(propertyName = "destinationType",
      propertyValue = "javax.jms.Queue")
})
public final class FeedbackBean implements MessageListener {

  @Resource
  private MessageDrivenContext mdc;
  private static final Logger LOGGER = Logger.getLogger(FeedbackBean.class.getCanonicalName());

  private SideInfoAwareDataModelBean model;

  public FeedbackBean() throws IOException, SolrServerException {
    model = SideInfoAwareDataModelBean.getInstance();
  }

  @Override
  public void onMessage(Message inMessage) {

    try {
      if (inMessage instanceof TextMessage) {
        String message = inMessage.getBody(String.class);

        if (model != null) {
          String[] parts = message.split(",");
          long userId = Long.parseLong(parts[0]);
          long itemId = Long.parseLong(parts[1]);
          float value = Float.parseFloat(parts[2]);
          model.setPreference(userId, itemId, value);
        }

      } else {
        LOGGER.log(Level.WARNING,
           "Message of wrong type: {0}",
           inMessage.getClass().getName());
      }
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE,
         "SimpleMessageBean.onMessage: JMSException: {0}",
         e.toString());
      mdc.setRollbackOnly();
    } catch (TasteException e) {
      LOGGER.log(Level.SEVERE, "TasteException: {0}", e.toString());
      mdc.setRollbackOnly();

    }
  }
}
