package se.citerus.dddsample.application.messaging;

import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.service.DomainEventNotifier;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * JMS based implementation.
 */
public final class JmsDomainEventNotifierImpl implements DomainEventNotifier {

  private JmsOperations jmsOperations;
  private Destination cargoHandledTopic;
  private Destination misdirectedCargoTopic;
  private Destination deliveredCargoTopic;

  @Override
  public void cargoWasHandled(final HandlingEvent event) {
    final Cargo cargo = event.cargo();
    jmsOperations.send(cargoHandledTopic, new MessageCreator() {
      public Message createMessage(final Session session) throws JMSException {
        return session.createTextMessage(cargo.trackingId().idString());
      }
    });
  }

  @Override
  public void cargoWasMisdirected(final Cargo cargo) {
    jmsOperations.send(misdirectedCargoTopic, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(cargo.trackingId().idString());
      }
    });
  }

  @Override
  public void cargoHasArrived(final Cargo cargo) {
    jmsOperations.send(deliveredCargoTopic, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(cargo.trackingId().idString());
      }
    });
  }

  public void setJmsOperations(final JmsOperations jmsOperations) {
    this.jmsOperations = jmsOperations;
  }

  public void setCargoHandledTopic(final Destination cargoHandledTopic) {
    this.cargoHandledTopic = cargoHandledTopic;
  }

  public void setMisdirectedCargoTopic(Destination misdirectedCargoTopic) {
    this.misdirectedCargoTopic = misdirectedCargoTopic;
  }

  public void setDeliveredCargoTopic(Destination deliveredCargoTopic) {
    this.deliveredCargoTopic = deliveredCargoTopic;
  }
  
}
