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

  public void fireHandlingEventRegistered(final HandlingEvent event) {
    jmsOperations.send(destination, new MessageCreator() {

      public Message createMessage(Session session) throws JMSException {
        MapMessage message = session.createMapMessage();
        message.setStringProperty(TrackingId.class.getName(), event.cargo().trackingId().idString());
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
