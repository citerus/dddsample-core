package se.citerus.dddsample.service;

import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;

import javax.jms.*;

/**
 * JMS based implementation.
 */
public class JmsEventServiceImpl implements EventService {
  private JmsOperations jmsOperations;
  private Destination destination;
  public static final String TRACKING_ID_KEY = TrackingId.class.getName() + ".KEY";

  public void fireHandlingEventRegistered(final HandlingEvent event) {
    jmsOperations.send(destination, new MessageCreator() {
      // TODO: richer message type
      public Message createMessage(Session session) throws JMSException {
        MapMessage message = session.createMapMessage();
        message.setStringProperty(TRACKING_ID_KEY, event.cargo().trackingId().idString());
        return message;
      }

    });
  }
  
  public void setJmsOperations(JmsOperations jmsOperations) {
    this.jmsOperations = jmsOperations;
  }

  public void setDestination(Destination destination) {
    this.destination = destination;
  }
}
