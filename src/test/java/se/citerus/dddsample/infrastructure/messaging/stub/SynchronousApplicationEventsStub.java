package se.citerus.dddsample.infrastructure.messaging.stub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

public class SynchronousApplicationEventsStub implements ApplicationEvents {

  CargoInspectionService cargoInspectionService;
  private final Log logger = LogFactory.getLog(getClass());

  public void setCargoInspectionService(CargoInspectionService cargoInspectionService) {
    this.cargoInspectionService = cargoInspectionService;
  }

  @Override
  public void cargoWasHandled(HandlingEvent event) {
    logger.info("EVENT: cargo was handled: " + event);
    cargoInspectionService.inspectCargo(event.cargo().trackingId());
  }

  @Override
  public void cargoWasMisdirected(Cargo cargo) {
    logger.info("EVENT: cargo was misdirected");
  }

  @Override
  public void cargoHasArrived(Cargo cargo) {
    logger.info("EVENT: cargo has arrived: " + cargo.trackingId().idString());
  }

  @Override
  public void receivedHandlingEventRegistrationAttempt(HandlingEventRegistrationAttempt attempt) {
    logger.info("EVENT: received handling event registration attempt");
  }
}
