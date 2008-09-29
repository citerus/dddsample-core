package se.citerus.dddsample.application.persistence;

import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.LOAD;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.RECEIVE;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CargoRepositoryTest extends AbstractRepositoryTest {

  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  CarrierMovementRepository carrierMovementRepository;

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }

  public void testFindByCargoId() {
    Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
    assertEquals(HONGKONG, cargo.origin());
    assertEquals(HELSINKI, cargo.destination());

    DeliveryHistory dh = cargo.deliveryHistory();
    assertNotNull(dh);

    List<HandlingEvent> events = dh.eventsOrderedByCompletionTime();
    assertEquals(2, events.size());

    HandlingEvent firstEvent = events.get(0);
    assertHandlingEvent(cargo, firstEvent, RECEIVE, HONGKONG, 100, 160, CarrierMovement.NONE);

    HandlingEvent secondEvent = events.get(1);
    CarrierMovement expectedCm = new CarrierMovement(new CarrierMovementId("CAR_010"), HONGKONG, MELBOURNE, new Date(), new Date());
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

    Date expectedCompletionTime = SampleDataGenerator.offset(completionTimeMs);
    assertEquals(expectedCompletionTime, event.completionTime());

    Date expectedRegistrationTime = SampleDataGenerator.offset(registrationTimeMs);
    assertEquals(expectedRegistrationTime, event.registrationTime());
    
    assertEquals(expectedCarrierMovement, event.carrierMovement());
    assertEquals(cargo, event.cargo());
  }

  public void testFindByCargoIdUnknownId() {
    assertNull(cargoRepository.find(new TrackingId("UNKNOWN")));
  }

  private void assertLeg(Leg firstLeg, String cmId, Location expectedFrom, Location expectedTo) {
    assertEquals(new CarrierMovementId(cmId), firstLeg.carrierMovement().carrierMovementId());
    assertEquals(expectedFrom, firstLeg.from());
    assertEquals(expectedTo, firstLeg.to());
  }

  public void testSave() {
    TrackingId trackingId = new TrackingId("AAA");
    Location origin = locationRepository.find(STOCKHOLM.unLocode());
    Location destination = locationRepository.find(MELBOURNE.unLocode());

    Cargo cargo = new Cargo(trackingId, origin, destination);
    cargoRepository.save(cargo);

    flush();

    Map<String, Object> map = sjt.queryForMap(
      "select * from Cargo where tracking_id = ?", trackingId.idString());

    assertEquals("AAA", map.get("TRACKING_ID"));

    Long originId = getLongId(origin);
    assertEquals(originId, map.get("ORIGIN_ID"));

    Long destinationId = getLongId(destination);
    assertEquals(destinationId, map.get("DESTINATION_ID"));

    assertNull(map.get("ITINERARY_ID"));
  }

  public void testDeleteOrphanedItinerary() {
    Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
    Long itineraryId = getLongId(cargo.itinerary());

    assertEquals(1, sjt.queryForInt("select count(*) from Itinerary where id = ?", itineraryId));

    cargo.detachItinerary();
    cargoRepository.save(cargo);
    flush();

    // Repository is responsible for deleting orphaned, detached itineraries
    assertEquals(0, sjt.queryForInt("select count(*) from Itinerary where id = ?", itineraryId));
  }

  public void testReplaceItinerary() {
    Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
    Long oldItineraryId = getLongId(cargo.itinerary());
    assertEquals(1, sjt.queryForInt("select count(*) from Itinerary where id = ?", oldItineraryId));

    CarrierMovement cm = carrierMovementRepository.find(new CarrierMovementId("CAR_006"));
    Location legFrom = locationRepository.find(new UnLocode("FIHEL"));
    Location legTo = locationRepository.find(new UnLocode("DEHAM"));
    Itinerary newItinerary = new Itinerary(Arrays.asList(new Leg(cm, legFrom, legTo)));

    cargo.attachItinerary(newItinerary);

    cargoRepository.save(cargo);
    flush();

    // Old itinerary should be deleted
    assertEquals(0, sjt.queryForInt("select count(*) from Itinerary where id = ?", oldItineraryId));

    // New itinerary should be cascade-saved
    Long newItineraryId = getLongId(cargo.itinerary());
    assertEquals(1, sjt.queryForInt("select count(*) from Itinerary where id = ?", newItineraryId));
  }


  public void testSaveShouldNotCascadeToHandlingEvents() {
    Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
    int eventCount = cargo.deliveryHistory().eventsOrderedByCompletionTime().size();

    Location origin = locationRepository.find(STOCKHOLM.unLocode());

    HandlingEvent event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, origin);
    assertFalse(cargo.deliveryHistory().eventsOrderedByCompletionTime().contains(event));

    CargoTestHelper.setDeliveryHistory(cargo, Arrays.asList(event));
    assertTrue(cargo.deliveryHistory().eventsOrderedByCompletionTime().contains(event));

    // Save cargo, evict from session and then re-load it - should not pick up the added event,
    // as it was never cascade-saved
    cargoRepository.save(cargo);
    getSession().evict(cargo);

    cargo = cargoRepository.find(cargo.trackingId());
    assertFalse(cargo.deliveryHistory().eventsOrderedByCompletionTime().contains(event));
    assertEquals(eventCount, cargo.deliveryHistory().eventsOrderedByCompletionTime().size());
  }

  public void testFindAll() {
    List<Cargo> all = cargoRepository.findAll();
    assertNotNull(all);
    assertEquals(6, all.size());
  }

  public void testNextTrackingId() {
    TrackingId trackingId = cargoRepository.nextTrackingId();
    assertNotNull(trackingId);

    TrackingId trackingId2 = cargoRepository.nextTrackingId();
    assertNotNull(trackingId2);
    assertFalse(trackingId.equals(trackingId2));
  }

}