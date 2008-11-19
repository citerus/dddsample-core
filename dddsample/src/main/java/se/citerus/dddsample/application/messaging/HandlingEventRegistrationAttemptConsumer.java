package se.citerus.dddsample.application.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.application.HandlingEventService;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * Consumes handling event registration attempt messages and delegates to
 * proper registration.
 * 
 */
public class HandlingEventRegistrationAttemptConsumer implements MessageListener {

  private HandlingEventService handlingEventService;
  private static final Log logger = LogFactory.getLog(HandlingEventRegistrationAttemptConsumer.class);

  @Override
  public void onMessage(final Message message) {
    try {
      final ObjectMessage om = (ObjectMessage) message;
      handlingEventService.register((HandlingEventRegistrationAttempt) om.getObject());
    } catch (Exception e) {
      logger.error(e, e);
    }
  }

  public void setHandlingEventService(HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
  }

}
