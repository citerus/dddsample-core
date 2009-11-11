package se.citerus.dddsample.tracking.core.infrastructure.reporting;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.*;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.ROTTERDAM;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.CargoRepositoryInMem;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.HandlingEventRepositoryInMem;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

public class ReportsUpdaterTest {

  ReportsUpdater reportsUpdater;
  WebClient client;
  EventSequenceNumber eventSequenceNumber;

  @Before
  public void setUp() {
    client = mock(WebClient.class);
    CargoRepository cargoRepository = new CargoRepositoryInMem();
    HandlingEventRepository handlingEventRepository = new HandlingEventRepositoryInMem();
    HandlingEventFactory handlingEventFactory = new HandlingEventFactory(cargoRepository, new VoyageRepositoryInMem(), new LocationRepositoryInMem());

    TrackingId trackingId = new TrackingId("ABC");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, ROTTERDAM, toDate("2009-10-10"));
    Cargo cargo = new Cargo(trackingId, routeSpecification);
    cargoRepository.store(cargo);

    HandlingEvent handlingEvent = handlingEventFactory.createHandlingEvent(
      toDate("2009-10-02"), trackingId, null, HONGKONG.unLocode(), HandlingEvent.Type.RECEIVE, new OperatorCode("ABCDE")
    );
    handlingEventRepository.store(handlingEvent);

    cargo.handled(handlingEvent.activity(), handlingEvent.completionTime());

    reportsUpdater = new ReportsUpdater(client, cargoRepository, handlingEventRepository, "/handling", "/cargo");
    eventSequenceNumber = handlingEvent.sequenceNumber();
  }

  @Test
  public void reportCargoUpdate() {
    when(client.path("/cargo")).thenReturn(client);

    reportsUpdater.reportCargoUpdate(new TrackingId("ABC"));

    CargoDetails expected = new CargoDetails();
    expected.setTrackingId("ABC");
    expected.setCurrentLocation("Hongkong");
    expected.setFinalDestination("Rotterdam");
    expected.setCurrentStatus("IN_PORT");

    verify(client).post(eq(expected));
  }

  @Test
  public void reportHandling() {
    when(client.path("/handling")).thenReturn(client);

    reportsUpdater.reportHandlingEvent(eventSequenceNumber);
    
    Handling expected = new Handling();
    expected.setLocation("Hongkong");
    expected.setType("RECEIVE");
    expected.setVoyage("");
    verify(client).post(eq(expected));
  }

}
