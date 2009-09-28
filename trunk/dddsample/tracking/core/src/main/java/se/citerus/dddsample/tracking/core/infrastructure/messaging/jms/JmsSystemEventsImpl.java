package se.citerus.dddsample.tracking.core.infrastructure.messaging.jms;

import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import se.citerus.dddsample.tracking.core.application.event.SystemEvents;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * JMS based implementation.
 */
public final class JmsSystemEventsImpl implements SystemEvents {

  private final JmsOperations jmsOperations;
  private final Destination cargoHandledDestination;
  private final Destination cargoUpdateDestination;

  public JmsSystemEventsImpl(final JmsOperations jmsOperations,
                             final Destination cargoHandledDestination,
                             final Destination cargoUpdateDestination) {
    this.jmsOperations = jmsOperations;
    this.cargoHandledDestination = cargoHandledDestination;
    this.cargoUpdateDestination = cargoUpdateDestination;
  }

  @Override
  public void notifyOfHandlingEvent(final HandlingEvent event) {
    final Cargo cargo = event.cargo();
    jmsOperations.send(cargoHandledDestination, new MessageCreator() {
      public Message createMessage(final Session session) throws JMSException {
        return session.createObjectMessage(cargo.trackingId());
      }
    });
  }

  @Override
  public void notifyOfCargoUpdate(final Cargo cargo) {
    jmsOperations.send(cargoUpdateDestination, new MessageCreator() {
      public Message createMessage(final Session session) throws JMSException {
        return session.createObjectMessage(cargo.trackingId());
      }
    });
  }

}
