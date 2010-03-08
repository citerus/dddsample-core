package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import se.citerus.dddsample.tracking.core.application.util.SampleDataGenerator;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType.LOAD;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType.RECEIVE;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.atlantic2;

public class CargoRepositoryTest extends AbstractRepositoryTest {

  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  VoyageRepository voyageRepository;

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setVoyageRepository(VoyageRepository voyageRepository) {
    this.voyageRepository = voyageRepository;
  }

  public void testFindByCargoId() {
    final TrackingId trackingId = new TrackingId("FGH");
    final Cargo cargo = cargoRepository.find(trackingId);
//	TODO: Remove the database column where the cargo origin used to be stored. Only the spec has origin now.
//			And then remove the following line from the test.    
//  assertEquals(STOCKHOLM, cargo.origin());
    assertEquals(HONGKONG, cargo.routeSpecification().origin());
    assertEquals(HELSINKI, cargo.routeSpecification().destination());

    final List<HandlingEvent> events = handlingEventRepository.lookupHandlingHistoryOfCargo(cargo).distinctEventsByCompletionTime();
    assertEquals(2, events.size());

    HandlingEvent firstEvent = events.get(0);
    assertHandlingEvent(cargo, firstEvent, RECEIVE, HONGKONG, 100, 160, Voyage.NONE);

    HandlingEvent secondEvent = events.get(1);

    Voyage hongkongMelbourneTokyoAndBack = new Voyage.Builder(
      new VoyageNumber("0303"), HONGKONG).
      addMovement(MELBOURNE, new Date(1), new Date(2)).
      addMovement(TOKYO, new Date(3), new Date(4)).
      addMovement(HONGKONG, new Date(5), new Date(6)).
      build();

    assertHandlingEvent(cargo, secondEvent, LOAD, HONGKONG, 150, 110, hongkongMelbourneTokyoAndBack);

    List<Leg> legs = cargo.itinerary().legs();
    assertEquals(3, legs.size());

    Leg firstLeg = legs.get(0);
    assertLeg(firstLeg, "0101", HONGKONG, MELBOURNE);

    Leg secondLeg = legs.get(1);
    assertLeg(secondLeg, "0101", MELBOURNE, STOCKHOLM);

    Leg thirdLeg = legs.get(2);
    assertLeg(thirdLeg, "0101", STOCKHOLM, HELSINKI);
  }

  private void assertHandlingEvent(Cargo cargo, HandlingEvent event, HandlingActivityType expectedEventType, Location expectedLocation, int completionTimeMs, int registrationTimeMs, Voyage voyage) {
    assertEquals(expectedEventType, event.type());
    assertEquals(expectedLocation, event.location());

    Date expectedCompletionTime = SampleDataGenerator.offset(completionTimeMs);
    assertEquals(expectedCompletionTime, event.completionTime());

    Date expectedRegistrationTime = SampleDataGenerator.offset(registrationTimeMs);
    assertEquals(expectedRegistrationTime, event.registrationTime());

    assertEquals(voyage, event.voyage());
    assertEquals(cargo, event.cargo());
  }

  public void testFindByCargoIdUnknownId() {
    assertNull(cargoRepository.find(new TrackingId("UNKNOWN")));
  }

  private void assertLeg(Leg firstLeg, String vn, Location expectedFrom, Location expectedTo) {
    assertEquals(new VoyageNumber(vn), firstLeg.voyage().voyageNumber());
    assertEquals(expectedFrom, firstLeg.loadLocation());
    assertEquals(expectedTo, firstLeg.unloadLocation());
  }

  public void testSave() {
    TrackingId trackingId = new TrackingId("AAA");
    Location origin = locationRepository.find(STOCKHOLM.unLocode());
    Location destination = locationRepository.find(MELBOURNE.unLocode());

    Cargo cargo = new Cargo(trackingId, new RouteSpecification(origin, destination, new Date()));
    cargoRepository.store(cargo);

    cargo.assignToRoute(new Itinerary(
      Leg.deriveLeg(
        voyageRepository.find(new VoyageNumber("0101")),
        locationRepository.find(STOCKHOLM.unLocode()),
        locationRepository.find(MELBOURNE.unLocode()))
    ));

    flush();

    Map<String, Object> map = sjt.queryForMap(
      "select * from Cargo where tracking_id = ?", trackingId.stringValue());

    assertEquals("AAA", map.get("TRACKING_ID"));

    Long originId = getLongId(origin);
    assertEquals(originId, map.get("SPEC_ORIGIN_ID"));

    Long destinationId = getLongId(destination);
    assertEquals(destinationId, map.get("SPEC_DESTINATION_ID"));

    getSession().clear();

    final Cargo loadedCargo = cargoRepository.find(trackingId);
    assertEquals(1, loadedCargo.itinerary().legs().size());
  }

  public void testReplaceItinerary() {
    Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
    Long cargoId = getLongId(cargo);
    assertEquals(3, sjt.queryForInt("select count(*) from Leg where cargo_id = ?", cargoId));

    Location legFrom = locationRepository.find(new UnLocode("DEHAM"));
    Location legTo = locationRepository.find(new UnLocode("FIHEL"));
    Itinerary newItinerary = new Itinerary(
        Leg.deriveLeg(atlantic2, legFrom, legTo)
    );

    cargo.assignToRoute(newItinerary);

    cargoRepository.store(cargo);
    flush();

    assertEquals(1, sjt.queryForInt("select count(*) from Leg where cargo_id = ?", cargoId));
  }


  public void testFindAll() {
    List<Cargo> all = cargoRepository.findAll();
    assertNotNull(all);
    assertEquals(6, all.size());
  }

  public void testFindCargosOnVoyage() {
    Voyage voyage = voyageRepository.find(new VoyageNumber("0101"));
    List<Cargo> cargos = cargoRepository.findCargosOnVoyage(voyage);
    assertEquals(3, cargos.size());
    for (Cargo cargo : cargos) {
      boolean found = false;
      for (Leg leg : cargo.itinerary().legs()) {
        if (leg.voyage().sameAs(voyage)) {
          found = true;
        }
      }
      assertTrue("Cargo " + cargo + " has no leg on voyage " + voyage, found);
    }
    
    Voyage voyage2 = voyageRepository.find(new VoyageNumber("0100S"));
    assertTrue(cargoRepository.findCargosOnVoyage(voyage2).isEmpty());
  }

}