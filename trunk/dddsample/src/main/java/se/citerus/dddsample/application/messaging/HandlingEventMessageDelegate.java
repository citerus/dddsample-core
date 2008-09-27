package se.citerus.dddsample.application.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.service.TrackingService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Consumes JMS messages and delegates notification of misdirected
 * cargo to the cargo service.
 */
public class HandlingEventMessageDelegate implements MessageListener {

  private TrackingService trackingService;
  private final Log logger = LogFactory.getLog(getClass());

  @Transactional(readOnly = true)  
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
