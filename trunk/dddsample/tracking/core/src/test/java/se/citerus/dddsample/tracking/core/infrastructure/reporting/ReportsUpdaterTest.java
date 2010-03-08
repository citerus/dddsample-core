package se.citerus.dddsample.tracking.core.infrastructure.reporting;

import org.junit.Before;
import org.junit.Test;
import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;
import se.citerus.dddsample.reporting.api.ReportSubmission;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.*;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.CargoRepositoryInMem;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.HandlingEventRepositoryInMem;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.ROTTERDAM;

public class ReportsUpdaterTest {

  ReportPusher reportPusher;
  ReportSubmission reportSubmission;
  EventSequenceNumber eventSequenceNumber;

  @Before
  public void setUp() {
    reportSubmission = mock(ReportSubmission.class);
    CargoRepository cargoRepository = new CargoRepositoryInMem();
    HandlingEventRepository handlingEventRepository = new HandlingEventRepositoryInMem();
    HandlingEventFactory handlingEventFactory = new HandlingEventFactory(cargoRepository, new VoyageRepositoryInMem(), new LocationRepositoryInMem());

    TrackingId trackingId = new TrackingId("ABC");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, ROTTERDAM, toDate("2009-10-10"));
    Cargo cargo = new Cargo(trackingId, routeSpecification);
    cargoRepository.store(cargo);

    HandlingEvent handlingEvent = handlingEventFactory.createHandlingEvent(
      toDate("2009-10-02"), trackingId, null, HONGKONG.unLocode(), HandlingActivityType.RECEIVE, new OperatorCode("ABCDE")
    );
    handlingEventRepository.store(handlingEvent);

    cargo.handled(handlingEvent.activity());

    reportPusher = new ReportPusher(reportSubmission, cargoRepository, handlingEventRepository);
    eventSequenceNumber = handlingEvent.sequenceNumber();
  }

  @Test
  public void reportCargoUpdate() {
    reportPusher.reportCargoUpdate(new TrackingId("ABC"));

    CargoDetails expected = new CargoDetails();
    expected.setTrackingId("ABC");
    expected.setCurrentLocation("Hongkong");
    expected.setFinalDestination("Rotterdam");
    expected.setCurrentStatus("IN_PORT");

    verify(reportSubmission).submitCargoDetails(eq(expected));
  }

  @Test
  public void reportHandling() {
    reportPusher.reportHandlingEvent(eventSequenceNumber);
    
    Handling expected = new Handling();
    expected.setLocation("Hongkong");
    expected.setType("RECEIVE");
    expected.setVoyage("");

    verify(reportSubmission).submitHandling(eq("ABC"), eq(expected));
  }

}
