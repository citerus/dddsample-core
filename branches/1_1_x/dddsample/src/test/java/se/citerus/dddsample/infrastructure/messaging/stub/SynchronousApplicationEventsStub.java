package se.citerus.dddsample.infrastructure.messaging.stub;

import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

public class SynchronousApplicationEventsStub implements ApplicationEvents {

  CargoInspectionService cargoInspectionService;

  public void setCargoInspectionService(CargoInspectionService cargoInspectionService) {
    this.cargoInspectionService = cargoInspectionService;
  }

  @Override
  public void cargoWasHandled(HandlingEvent event) {
    System.out.println("EVENT: cargo was handled: " + event);
    cargoInspectionService.inspectCargo(event.cargo().trackingId());
  }

  @Override
  public void cargoWasMisdirected(Cargo cargo) {
    System.out.println("EVENT: cargo was misdirected");
  }

  @Override
  public void cargoHasArrived(Cargo cargo) {
    System.out.println("EVENT: cargo has arrived: " + cargo.trackingId().idString());
  }

  @Override
  public void receivedHandlingEventRegistrationAttempt(HandlingEventRegistrationAttempt attempt) {
    System.out.println("EVENT: received handling event registration attempt");
  }
}
