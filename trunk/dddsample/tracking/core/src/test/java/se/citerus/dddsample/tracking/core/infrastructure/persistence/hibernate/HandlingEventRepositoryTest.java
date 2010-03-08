package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.MELBOURNE;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.claimIn;

public class HandlingEventRepositoryTest extends AbstractRepositoryTest {

  HandlingEventRepository handlingEventRepository;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  HandlingEventFactory handlingEventFactory;
  VoyageRepository voyageRepository;

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setVoyageRepository(VoyageRepository voyageRepository) {
    this.voyageRepository = voyageRepository;
  }

  public void testSave() {
    handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    UnLocode unLocode = new UnLocode("SESTO");
    Location location = locationRepository.find(unLocode);

    TrackingId trackingId = new TrackingId("XYZ");
    Date completionTime = new Date(10);
    HandlingEvent event = handlingEventFactory.createHandlingEvent(completionTime, trackingId, null, unLocode, HandlingActivityType.CLAIM, null);

    handlingEventRepository.store(event);

    flush();

    Map<String, Object> result = sjt.queryForMap("select * from HandlingEvent where sequence_number = ?", event.sequenceNumber());
    assertEquals(1L, result.get("CARGO_ID"));
    assertEquals(new Date(10), result.get("COMPLETIONTIME"));
    // TODO: the rest of the columns
  }

  public void testFindEventsForCargo() throws Exception {
    Cargo cargo = cargoRepository.find(new TrackingId("XYZ"));
    List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(cargo).distinctEventsByCompletionTime();
    assertEquals(12, handlingEvents.size());
  }

  public void testMostRecentHandling() {
    Cargo cargo = cargoRepository.find(new TrackingId("XYZ"));
    HandlingEvent handlingEvent = handlingEventRepository.mostRecentHandling(cargo);
    assertEquals(cargo, handlingEvent.cargo());
    assertEquals(toDate("2007-09-27", "05:00"), handlingEvent.completionTime());

    assertEquals(claimIn(MELBOURNE), handlingEvent.activity());
    assertEquals(handlingEvent.activity(), claimIn(MELBOURNE));
  }

}