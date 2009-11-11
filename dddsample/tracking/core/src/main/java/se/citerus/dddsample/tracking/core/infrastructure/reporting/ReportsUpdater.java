package se.citerus.dddsample.tracking.core.infrastructure.reporting;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.EventSequenceNumber;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;

@Service
public class ReportsUpdater {

  private WebClient webClient;
  private CargoRepository cargoRepository;
  private HandlingEventRepository handlingEventRepository;
  private String handlingPath;
  private String cargoDetailsPath;

  @Autowired
  public ReportsUpdater(final WebClient webClient,
                        final CargoRepository cargoRepository,
                        final HandlingEventRepository handlingEventRepository,
                        final String handlingPath,
                        final String cargoDetailsPath) {
    this.webClient = webClient;
    this.cargoRepository = cargoRepository;
    this.handlingEventRepository = handlingEventRepository;
    this.handlingPath = handlingPath;
    this.cargoDetailsPath = cargoDetailsPath;
  }

  @Transactional
  public void reportHandlingEvent(final EventSequenceNumber sequenceNumber) {
    HandlingEvent handlingEvent = handlingEventRepository.find(sequenceNumber);
    Handling handling = toHandling(handlingEvent);
    webClient.path(handlingPath).post(handling);
  }

  @Transactional
  public void reportCargoUpdate(final TrackingId trackingId) {
    Cargo cargo = cargoRepository.find(trackingId);
    CargoDetails cargoDetails = toCargoDetails(cargo);
    webClient.path(cargoDetailsPath).post(cargoDetails);
  }

  private Handling toHandling(HandlingEvent handlingEvent) {
    Handling handling = new Handling();
    handling.setLocation(handlingEvent.location().name());
    handling.setType(handlingEvent.activity().type().name());
    handling.setVoyage(handlingEvent.voyage().voyageNumber().stringValue());
    return handling;
  }

  private CargoDetails toCargoDetails(Cargo cargo) {
    CargoDetails cargoDetails = new CargoDetails();
    cargoDetails.setTrackingId(cargo.trackingId().stringValue());
    cargoDetails.setFinalDestination(cargo.routeSpecification().destination().name());
    cargoDetails.setCurrentLocation(cargo.lastKnownLocation().name());
    cargoDetails.setCurrentStatus(cargo.transportStatus().name());
    return cargoDetails;
  }

}
