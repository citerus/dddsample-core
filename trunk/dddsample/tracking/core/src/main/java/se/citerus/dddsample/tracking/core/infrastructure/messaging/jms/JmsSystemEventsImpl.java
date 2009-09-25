package se.citerus.dddsample.tracking.core.infrastructure.messaging.jms;

import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Service
public final class JmsSystemEventsImpl implements SystemEvents {

  private final JmsOperations jmsOperations;
  private final Destination cargoHandledDestination;
  private final Destination cargoUpdateDestination;

  @Autowired
  public JmsSystemEventsImpl(final JmsOperations jmsOperations,
                             final @Qualifier("cargoHandledDestination") Destination cargoHandledDestination,
                             final @Qualifier("cargoUpdateDestination") Destination cargoUpdateDestination) {
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
