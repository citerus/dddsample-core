package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.*;

import java.util.Date;
import java.util.Map;

public class HandlingEventRepositoryTest extends AbstractRepositoryTest {

  HandlingEventRepository handlingEventRepository;
  CargoRepository cargoRepository;

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void testSave() {
    // TODO: introduce Location repository
    Location location = new Location("ABC");
    sessionFactory.getCurrentSession().saveOrUpdate(location);

    Cargo cargo = cargoRepository.find(new TrackingId("XYZ"));
    Date completionTime = new Date(10);
    Date registrationTime = new Date(20);
    HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, HandlingEvent.Type.CLAIM, location);

    handlingEventRepository.save(event);

    flush();

    Map<String,Object> result = sjt.queryForMap("select * from HandlingEvent where id = ?", event.id());
    assertEquals("XYZ", result.get("CARGO_ID"));
    assertEquals(new Date(10), result.get("COMPLETIONTIME"));
    assertEquals(new Date(20), result.get("REGISTRATIONTIME"));
    assertEquals("CLAIM", result.get("TYPE"));
    // TODO: the rest of the columns
  }

  public void testFindDeliveryHistory() {
    DeliveryHistory dh = handlingEventRepository.findDeliveryHistory(new TrackingId("XYZ"));

    assertNotNull(dh);
    assertEquals(1, dh.eventsOrderedByTime().size());
    HandlingEvent lastEvent = dh.lastEvent();
    assertNotNull(lastEvent);
    assertEquals("SESTO", lastEvent.location().unlocode());
    // TODO: the rest of the properties, and maybe a longer list of events
  }
}