package se.citerus.dddsample.application.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;

import javax.jms.*;

/**
 */
public class HandlingEventRegistrationAttemptConsumer implements MessageListener {

  private HandlingEventService handlingEventService;
  private JmsOperations jmsOperations;
  private Destination rejectedRegistrationAttemptsQueue;
  private static final Log logger = LogFactory.getLog(HandlingEventRegistrationAttemptConsumer.class);

  @Override
  public void onMessage(final Message message) {
    try {
      final ObjectMessage om = (ObjectMessage) message;
      final HandlingEventRegistrationAttempt attempt = (HandlingEventRegistrationAttempt) om.getObject();
      try {
        handlingEventService.register(attempt);
      } catch (CannotCreateHandlingEventException e) {
        jmsOperations.send(rejectedRegistrationAttemptsQueue, new MessageCreator() {
          public Message createMessage(Session session) throws JMSException {
            
            return session.createObjectMessage(attempt);
          }
        });
      }
    } catch (JMSException e) {
      logger.error(e, e);
    }
  }

  public void setHandlingEventService(HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
  }

  public void setJmsOperations(JmsOperations jmsOperations) {
    this.jmsOperations = jmsOperations;
  }

  public void setRejectedRegistrationAttemptsQueue(Destination rejectedRegistrationAttemptsQueue) {
    this.rejectedRegistrationAttemptsQueue = rejectedRegistrationAttemptsQueue;
  }
}
