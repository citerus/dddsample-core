package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.MELBOURNE;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class HandlingEventRepositoryTest extends AbstractRepositoryTest {

  HandlingEventRepository handlingEventRepository;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void testSave() {
    Location location = locationRepository.find(new UnLocode("SESTO"));

    Cargo cargo = cargoRepository.find(new TrackingId("XYZ"));
    Date completionTime = new Date(10);
    Date registrationTime = new Date(20);
    HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, HandlingEvent.Type.CLAIM, location);

    handlingEventRepository.store(event);

    flush();

    Map<String, Object> result = sjt.queryForMap("select * from HandlingEvent where id = ?", getLongId(event));
    assertEquals(1L, result.get("CARGO_ID"));
    assertEquals(new Date(10), result.get("COMPLETIONTIME"));
    assertEquals(new Date(20), result.get("REGISTRATIONTIME"));
    assertEquals("CLAIM", result.get("TYPE"));
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
    assertEquals(new HandlingActivity(HandlingEvent.Type.CLAIM, MELBOURNE), handlingEvent.activity());
  }

}