package se.citerus.dddsample.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.TrackingId;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Consumes JMS messages and delegates notification of misdirected
 * cargo to the cargo service.
 *
 * This point of this is to decouple the cargo service from JMS,
 * and to allow a thread-based messaging implementation to live in
 * parallell. 
 */
public class HandlingEventMessageDelegate implements MessageListener {

  CargoService cargoService;
  private static final Log logger = LogFactory.getLog(HandlingEventMessageDelegate.class);

  public void onMessage(Message message) {
    logger.info("Received message " + message);
    try {
      String tidString = message.getStringProperty(TrackingId.class.getName());
      cargoService.notifyIfMisdirected(new TrackingId(tidString));
    } catch (JMSException e) {
      logger.error(e, e);
    }
  }

  public void setCargoService(CargoService cargoService) {
    this.cargoService = cargoService;
  }
}
