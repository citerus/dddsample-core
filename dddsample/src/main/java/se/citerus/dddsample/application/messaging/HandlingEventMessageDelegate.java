package se.citerus.dddsample.application.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.service.TrackingService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Consumes JMS messages and delegates notification of misdirected
 * cargo to the cargo service.
 * <p/>
 * This point of this is to decouple the tracking service from JMS,
 * and to allow a thread-based messaging implementation to live in
 * parallell.
 */
public class HandlingEventMessageDelegate implements MessageListener {

  private TrackingService trackingService;
  private final Log logger = LogFactory.getLog(getClass());

  public void onMessage(final Message message) {
    if (logger.isDebugEnabled()) {
      logger.debug("Received message " + message);
    }
    try {
      String tidString = message.getStringProperty(JmsDomainEventNotifierImpl.TRACKING_ID_KEY);
      trackingService.inspectCargo(new TrackingId(tidString));
    } catch (JMSException e) {
      logger.error(e, e);
    }
  }

  public void setTrackingService(TrackingService trackingService) {
    this.trackingService = trackingService;
  }
}
