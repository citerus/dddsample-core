package se.citerus.dddsample.tracking.core.application.event;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.*;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.LOAD;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.GOTHENBURG;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.HONGKONG_TO_NEW_YORK;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.*;

public class CargoUpdaterTest {

  SystemEvents systemEvents;
  CargoUpdater cargoUpdater;
  HandlingEventFactory handlingEventFactory;
  CargoRepository cargoRepository;
  HandlingEventRepository handlingEventRepository;
  LocationRepository locationRepository;
  VoyageRepository voyageRepository;
  TrackingIdFactoryInMem trackingIdFactory;

  @Before
  public void setUp() {
    systemEvents = mock(SystemEvents.class);
    cargoRepository = new CargoRepositoryInMem();
    handlingEventRepository = new HandlingEventRepositoryInMem();
    locationRepository = new LocationRepositoryInMem();
    voyageRepository = new VoyageRepositoryInMem();
    trackingIdFactory = new TrackingIdFactoryInMem();
    handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    cargoUpdater = new CargoUpdater(systemEvents, cargoRepository, handlingEventRepository);
  }

  @Test
  public void updateCargo() throws CannotCreateHandlingEventException {
    TrackingId trackingId = trackingIdFactory.nextTrackingId();
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, GOTHENBURG, toDate("2009-10-15"));

    Cargo cargo = new Cargo(trackingId, routeSpecification);
    cargoRepository.store(cargo);

    HandlingEvent handlingEvent = handlingEventFactory.createHandlingEvent(
      toDate("2009-10-01", "14:30"),
      cargo.trackingId(),
      HONGKONG_TO_NEW_YORK.voyageNumber(),
      HONGKONG.unLocode(),
      LOAD, new OperatorCode("ABCDE")
    );

    handlingEventRepository.store(handlingEvent);

    assertThat(handlingEvent.activity(), not(equalTo(cargo.mostRecentHandlingActivity())));

    cargoUpdater.updateCargo(handlingEvent.sequenceNumber());
    
    assertThat(handlingEvent.activity(), equalTo(cargo.mostRecentHandlingActivity()));
    verify(systemEvents).notifyOfCargoUpdate(cargo);
  }

  @Test
  public void handlingEventNotFound() {
    cargoUpdater.updateCargo(EventSequenceNumber.valueOf(999L));
    
    verifyZeroInteractions(systemEvents);
  }
  
}
