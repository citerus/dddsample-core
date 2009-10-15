package se.citerus.dddsample.tracking.core.application.event;

import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import org.junit.Before;
import org.junit.Test;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoFactory;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
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
  CargoFactory cargoFactory;
  HandlingEventFactory handlingEventFactory;
  CargoRepository cargoRepository;
  HandlingEventRepository handlingEventRepository;
  LocationRepository locationRepository;
  VoyageRepository voyageRepository;

  @Before
  public void setUp() throws CannotCreateHandlingEventException {
    systemEvents = createMock(SystemEvents.class);
    cargoRepository = new CargoRepositoryInMem();
    handlingEventRepository = new HandlingEventRepositoryInMem();
    locationRepository = new LocationRepositoryInMem();
    voyageRepository = new VoyageRepositoryInMem();
    cargoFactory = new CargoFactory(locationRepository, new TrackingIdGeneratorInMem());
    handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    cargoUpdater = new CargoUpdater(systemEvents, cargoRepository, handlingEventRepository);
  }

  @Test
  public void updateCargo() throws CannotCreateHandlingEventException {
    Cargo cargo = cargoFactory.newCargo(HONGKONG.unLocode(), GOTHENBURG.unLocode(), toDate("2009-10-15"));
    cargoRepository.store(cargo);

    HandlingEvent handlingEvent = handlingEventFactory.createHandlingEvent(
      toDate("2009-10-01", "14:30"),
      cargo.trackingId(),
      HONGKONG_TO_NEW_YORK.voyageNumber(),
      HONGKONG.unLocode(),
      LOAD
    );
    handlingEventRepository.store(handlingEvent);

    systemEvents.notifyOfCargoUpdate(cargo);
    replay(systemEvents);

    assertThat(handlingEvent.activity(), not(equalTo(cargo.mostRecentHandlingActivity())));

    cargoUpdater.updateCargo(handlingEvent.sequenceNumber());
    
    assertThat(handlingEvent.activity(), equalTo(cargo.mostRecentHandlingActivity()));

    verify(systemEvents);
  }

  @Test
  public void handlingEventNotFound() {
    replay(systemEvents);

    cargoUpdater.updateCargo(EventSequenceNumber.valueOf(999L));
    
    verify(systemEvents);
  }
}
