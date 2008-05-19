package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.HandlingEvent.Type.LOAD;
import static se.citerus.dddsample.domain.HandlingEvent.Type.RECEIVE;
import static se.citerus.dddsample.domain.SampleLocations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CargoRepositoryTest extends AbstractRepositoryTest {

  CargoRepository cargoRepository;

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void testFindByCargoId() {
    Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
    assertEquals(HONGKONG, cargo.origin());
    assertEquals(HELSINKI, cargo.finalDestination());

    DeliveryHistory dh = cargo.deliveryHistory();
    assertNotNull(dh);

    List<HandlingEvent> events = dh.eventsOrderedByCompletionTime();
    assertEquals(2, events.size());

    HandlingEvent firstEvent = events.get(0);
    assertHandlingEvent(cargo, firstEvent, RECEIVE, HONGKONG, 100, 160, null);

    HandlingEvent secondEvent = events.get(1);
    CarrierMovement expectedCm = new CarrierMovement(new CarrierMovementId("CAR_010"), HONGKONG, MELBOURNE);
    assertHandlingEvent(cargo,  secondEvent, LOAD, HONGKONG, 150, 110, expectedCm);

    List<Leg> legs = cargo.itinerary().legs();
    assertEquals(3, legs.size());

    Leg firstLeg = legs.get(0);
    assertLeg(firstLeg, "CAR_010", HONGKONG, MELBOURNE);

    Leg secondLeg = legs.get(1);
    assertLeg(secondLeg, "CAR_011", MELBOURNE, STOCKHOLM);

    Leg thirdLeg = legs.get(2);
    assertLeg(thirdLeg, "CAR_011", STOCKHOLM, HELSINKI);
  }

  private void assertHandlingEvent(Cargo cargo, HandlingEvent event, HandlingEvent.Type expectedEventType, Location expectedLocation, int completionTimeMs, int registrationTimeMs, CarrierMovement expectedCarrierMovement) {
    assertEquals(expectedEventType, event.type());
    assertEquals(expectedLocation, event.location());
    assertEquals(new Date(completionTimeMs), event.completionTime());
    assertEquals(new Date(registrationTimeMs), event.registrationTime());
    if (expectedCarrierMovement == null) {
      assertNull(event.carrierMovement());
    } else {
      assertEquals(expectedCarrierMovement, event.carrierMovement());
    }
    assertEquals(cargo, event.cargo());
  }

  private void assertLeg(Leg firstLeg, String cmId, Location expectedFrom, Location expectedTo) {
    assertEquals(new CarrierMovementId(cmId), firstLeg.carrierMovementId());
    assertEquals(expectedFrom, firstLeg.from());
    assertEquals(expectedTo, firstLeg.to());
  }

  public void testSave() {
    sessionFactory.getCurrentSession().saveOrUpdate(STOCKHOLM);
    sessionFactory.getCurrentSession().saveOrUpdate(MELBOURNE);


    Cargo cargo = new Cargo(new TrackingId("AAA"), STOCKHOLM, MELBOURNE);
    cargoRepository.save(cargo);

    flush();

    Map<String, Object> map = sjt.queryForMap("select * from Cargo where tracking_id = 'AAA'");

    assertEquals("AAA", map.get("TRACKING_ID"));
    // TODO: check origin/finalDestination ids
  }

  public void testFindAll() {
    List<Cargo> all = cargoRepository.findAll();
    assertNotNull(all);
    assertEquals(6, all.size());
  }

}