package se.citerus.dddsample.tracking.core.infrastructure.reporting;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;
import se.citerus.dddsample.reporting.api.ReportSubmission;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.EventSequenceNumber;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;

public class ReportPusher {

  private CargoRepository cargoRepository;
  private HandlingEventRepository handlingEventRepository;
  private ReportSubmission reportSubmission;

  public ReportPusher(final ReportSubmission reportSubmission,
                      final CargoRepository cargoRepository,
                      final HandlingEventRepository handlingEventRepository) {
    this.reportSubmission = reportSubmission;
    this.cargoRepository = cargoRepository;
    this.handlingEventRepository = handlingEventRepository;
  }

  @Transactional
  public void reportHandlingEvent(final EventSequenceNumber sequenceNumber) {
    HandlingEvent handlingEvent = handlingEventRepository.find(sequenceNumber);
    Handling handling = assembleFrom(handlingEvent);
    String trackingIdString = handlingEvent.cargo().trackingId().stringValue();

    reportSubmission.submitHandling(trackingIdString, handling);
  }

  @Transactional
  public void reportCargoUpdate(final TrackingId trackingId) {
    Cargo cargo = cargoRepository.find(trackingId);
    CargoDetails cargoDetails = assembleFrom(cargo);

    reportSubmission.submitCargoDetails(cargoDetails);
  }

  private Handling assembleFrom(HandlingEvent handlingEvent) {
    Handling handling = new Handling();
    handling.setLocation(handlingEvent.location().name());
    handling.setType(handlingEvent.activity().type().name());
    handling.setVoyage(handlingEvent.voyage().voyageNumber().stringValue());
    return handling;
  }

  private CargoDetails assembleFrom(Cargo cargo) {
    CargoDetails cargoDetails = new CargoDetails();
    cargoDetails.setTrackingId(cargo.trackingId().stringValue());
    cargoDetails.setFinalDestination(cargo.routeSpecification().destination().name());
    cargoDetails.setCurrentLocation(cargo.lastKnownLocation().name());
    cargoDetails.setCurrentStatus(cargo.transportStatus().name());
    return cargoDetails;
  }

  ReportPusher() {
    // Needed by CGLIB
  }

}
