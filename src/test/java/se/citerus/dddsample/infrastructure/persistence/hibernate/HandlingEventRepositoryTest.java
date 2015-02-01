package se.citerus.dddsample.infrastructure.persistence.hibernate;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

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

    Map<String,Object> result = sjt.queryForMap("select * from HandlingEvent where id = ?", getLongId(event));
    assertEquals(1L, result.get("CARGO_ID"));
    assertEquals(new Date(10), result.get("COMPLETIONTIME"));
    assertEquals(new Date(20), result.get("REGISTRATIONTIME"));
    assertEquals("CLAIM", result.get("TYPE"));
    // TODO: the rest of the columns
  }

  public void testFindEventsForCargo() throws Exception {
    TrackingId trackingId = new TrackingId("XYZ");
    List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
    assertEquals(12, handlingEvents.size());
  }

}