package com.discovery.feedback.bean;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.Recommender;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
   @ActivationConfigProperty(propertyName = "destinationLookup",
      propertyValue = "jms/feedbackQueue"),
   @ActivationConfigProperty(propertyName = "destinationType",
      propertyValue = "javax.jms.Queue")
})
final class FeedbackBean implements MessageListener {

  @Resource
  private MessageDrivenContext mdc;
  private static final Logger LOGGER = Logger.getLogger("SimpleMessageBean");

  private Recommender recommender;

  public FeedbackBean() {
  }

  @Override
  public void onMessage(Message inMessage) {

    try {
      if (inMessage instanceof TextMessage) {
        String message = inMessage.getBody(String.class);
        LOGGER.log(Level.INFO,
           "MESSAGE BEAN: Message received: {0}", message);

        if (recommender != null) {
          String[] parts = message.split(",");
          long userId = Long.parseLong(parts[0]);
          long itemId = Long.parseLong(parts[1]);
          float value = Long.parseLong(parts[2]);
          recommender.setPreference(userId, itemId, value);
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
    }
  }
}
