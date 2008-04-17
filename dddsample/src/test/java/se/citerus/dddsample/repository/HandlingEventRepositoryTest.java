package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.*;

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
    Location location = locationRepository.find(new UnLocode("SE","STO"));

    Cargo cargo = cargoRepository.find(new TrackingId("XYZ"));
    Date completionTime = new Date(10);
    Date registrationTime = new Date(20);
    HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, HandlingEvent.Type.CLAIM, location, null);

    handlingEventRepository.save(event);

    flush();

    Map<String,Object> result = sjt.queryForMap("select * from HandlingEvent where id = ?", event.id());
    assertEquals(1L, result.get("CARGO_ID"));
    assertEquals(new Date(10), result.get("COMPLETIONTIME"));
    assertEquals(new Date(20), result.get("REGISTRATIONTIME"));
    assertEquals("CLAIM", result.get("TYPE"));
    // TODO: the rest of the columns
  }

  public void testFindEventsForCargo() throws Exception {
    List<HandlingEvent> handlingEvents = handlingEventRepository.findEventsForCargo(new TrackingId("XYZ"));
    assertEquals(12, handlingEvents.size());
  }

}