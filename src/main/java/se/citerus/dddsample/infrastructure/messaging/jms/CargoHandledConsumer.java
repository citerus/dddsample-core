package se.citerus.dddsample.infrastructure.messaging.jms;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.lang.invoke.MethodHandles;

/**
 * Consumes JMS messages and delegates notification of misdirected
 * cargo to the tracking service.
 *
 * This is a programmatic hook into the JMS infrastructure to
 * make cargo inspection message-driven.
 */
public class CargoHandledConsumer implements MessageListener {

  private final CargoInspectionService cargoInspectionService;
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public CargoHandledConsumer(CargoInspectionService cargoInspectionService) {
    this.cargoInspectionService = cargoInspectionService;
  }

  @Override
  public void onMessage(final Message message) {
    try {
      final TextMessage textMessage = (TextMessage) message;
      final String trackingidString = textMessage.getText();
      
      cargoInspectionService.inspectCargo(new TrackingId(trackingidString));
    } catch (Exception e) {
      logger.error("Error consuming CargoHandled message", e);
    }
  }
}
