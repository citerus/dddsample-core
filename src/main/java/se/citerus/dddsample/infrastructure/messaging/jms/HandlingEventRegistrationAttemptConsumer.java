package se.citerus.dddsample.infrastructure.messaging.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.lang.invoke.MethodHandles;

/**
 * Consumes handling event registration attempt messages and delegates to
 * proper registration.
 * 
 */
public class HandlingEventRegistrationAttemptConsumer implements MessageListener {

  private final HandlingEventService handlingEventService;
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public HandlingEventRegistrationAttemptConsumer(HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
  }

  @Override
  public void onMessage(final Message message) {
    try {
      final ObjectMessage om = (ObjectMessage) message;
      HandlingEventRegistrationAttempt attempt = (HandlingEventRegistrationAttempt) om.getObject();
      handlingEventService.registerHandlingEvent(
        attempt.getCompletionTime(),
        attempt.getTrackingId(),
        attempt.getVoyageNumber(),
        attempt.getUnLocode(),
        attempt.getType()
      );
    } catch (Exception e) {
      logger.error("Error consuming HandlingEventRegistrationAttempt message", e);
    }
  }
}
