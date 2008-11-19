package se.citerus.dddsample.application.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.application.TrackingService;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Consumes JMS messages and delegates notification of misdirected
 * cargo to the cargo service.
 */
public class CargoHandledConsumer implements MessageListener {

  private TrackingService trackingService;
  private final Log logger = LogFactory.getLog(getClass());

  @Override  
  public void onMessage(final Message message) {
    if (logger.isDebugEnabled()) {
      logger.debug("Received message " + message);
    }
    try {
      final TextMessage textMessage = (TextMessage) message;
      final String trackingidString = textMessage.getText();
      trackingService.onCargoHandled(new TrackingId(trackingidString));
    } catch (Exception e) {
      logger.error(e, e);
    }
  }

  public void setTrackingService(TrackingService trackingService) {
    this.trackingService = trackingService;
  }
}
