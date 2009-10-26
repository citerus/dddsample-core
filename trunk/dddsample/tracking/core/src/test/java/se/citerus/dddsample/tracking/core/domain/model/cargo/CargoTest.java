package se.citerus.dddsample.tracking.core.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.IN_PORT;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.NOT_RECEIVED;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;

import java.util.Arrays;
import java.util.Date;

public class CargoTest extends TestCase {

  private Voyage crazyVoyage = new Voyage.Builder(new VoyageNumber("0123"),
    STOCKHOLM).
    addMovement(HAMBURG, new Date(), new Date()).
    addMovement(HONGKONG, new Date(), new Date()).
    addMovement(MELBOURNE, new Date(), new Date()).
    build();

  private Voyage pacific = new Voyage.Builder(new VoyageNumber("4567"),
    SHANGHAI).
    addMovement(LONGBEACH, new Date(), new Date()).
    addMovement(SEATTLE, new Date(), new Date()).
    build();

  private Voyage transcontinental = new Voyage.Builder(new VoyageNumber("4567"),
    LONGBEACH).
    addMovement(CHICAGO, new Date(), new Date()).
    addMovement(NEWYORK, new Date(), new Date()).
    build();

  private Voyage northernRail = new Voyage.Builder(new VoyageNumber("8901"),
    SEATTLE).
    addMovement(CHICAGO, new Date(), new Date()).
    addMovement(NEWYORK, new Date(), new Date()).
    build();

  public void testConstruction() {
    TrackingId trackingId = new TrackingId("XYZ");
    Date arrivalDeadline = toDate("2009-03-13");
    RouteSpecification routeSpecification = new RouteSpecification(
      STOCKHOLM, MELBOURNE, arrivalDeadline
    );

    Cargo cargo = new Cargo(trackingId, routeSpecification);

    assertEquals(NOT_ROUTED, cargo.routingStatus());
    assertEquals(NOT_RECEIVED, cargo.transportStatus());
    assertEquals(Location.UNKNOWN, cargo.lastKnownLocation());
    assertEquals(Voyage.NONE, cargo.currentVoyage());
  }

  public void testRoutingStatus() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));
    final Itinerary good = new Itinerary(Leg.deriveLeg(northernRail, SEATTLE, NEWYORK));
    final Itinerary bad = new Itinerary(Leg.deriveLeg(crazyVoyage, HAMBURG, HONGKONG));
    final RouteSpecification acceptOnlyGood = new RouteSpecification(cargo.routeSpecification().origin(), cargo.routeSpecification().destination(), new Date()) {
      @Override
      public boolean isSatisfiedBy(Itinerary itinerary) {
        return itinerary == good;
      }
    };

    cargo.specifyNewRoute(acceptOnlyGood);

    assertEquals(NOT_ROUTED, cargo.routingStatus());

    cargo.assignToRoute(bad);
    assertEquals(MISROUTED, cargo.routingStatus());

    cargo.assignToRoute(good);
    assertEquals(ROUTED, cargo.routingStatus());
  }

  public void testOutOrderHandling() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    cargo.handled(HandlingActivity.loadedOnto(crazyVoyage).in(STOCKHOLM), toDate("2009-10-01"));
    cargo.handled(new HandlingActivity(LOAD, STOCKHOLM, crazyVoyage), toDate("2009-10-01"));
    cargo.handled(new HandlingActivity(UNLOAD, HAMBURG, crazyVoyage), toDate("2009-10-02"));
    cargo.handled(new HandlingActivity(UNLOAD, HONGKONG, crazyVoyage), toDate("2009-10-04"));
    assertEquals(cargo.transportStatus(), IN_PORT);
    assertEquals(cargo.lastKnownLocation(), HONGKONG);

    // Out of order handling, does not affect state of cargo
    cargo.handled(new HandlingActivity(LOAD, HAMBURG, crazyVoyage), toDate("2009-10-03"));
    assertEquals(IN_PORT, cargo.transportStatus());
    assertEquals(HONGKONG, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationUnknownWhenNoEvents() throws Exception {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    assertEquals(Location.UNKNOWN, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

    assertEquals(STOCKHOLM, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

    assertEquals(MELBOURNE, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

    assertEquals(HONGKONG, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

    assertEquals(HAMBURG, cargo.lastKnownLocation());
  }

  public void testEquality() throws Exception {
    RouteSpecification spec1 = new RouteSpecification(STOCKHOLM, HONGKONG, new Date());
    RouteSpecification spec2 = new RouteSpecification(STOCKHOLM, MELBOURNE, new Date());
    Cargo c1 = new Cargo(new TrackingId("ABC"), spec1);
    Cargo c2 = new Cargo(new TrackingId("CBA"), spec1);
    Cargo c3 = new Cargo(new TrackingId("ABC"), spec2);
    Cargo c4 = new Cargo(new TrackingId("ABC"), spec1);

    assertTrue("Cargos should be equal when TrackingIDs are equal", c1.equals(c4));
    assertTrue("Cargos should be equal when TrackingIDs are equal", c1.equals(c3));
    assertTrue("Cargos should be equal when TrackingIDs are equal", c3.equals(c4));
    assertFalse("Cargos are not equal when TrackingID differ", c1.equals(c2));
  }

  public void testIsUnloadedAtFinalDestination() throws Exception {
    Cargo cargo = setUpCargoWithItinerary(HANGZOU, TOKYO, NEWYORK);
    assertFalse(cargo.isReadyToClaim());

    // Adding an event unrelated to unloading at final destination
    cargo.handled(new HandlingActivity(RECEIVE, HANGZOU), new Date());
    assertFalse(cargo.isReadyToClaim());

    Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"), HANGZOU).
      addMovement(NEWYORK, new Date(), new Date()).
      build();

    // Adding an unload event, but not at the final destination
    cargo.handled(new HandlingActivity(UNLOAD, TOKYO, voyage), new Date());
    assertFalse(cargo.isReadyToClaim());

    // Adding an event in the final destination, but not unload
    cargo.handled(new HandlingActivity(CUSTOMS, NEWYORK), new Date());
    assertFalse(cargo.isReadyToClaim());

    // Finally, cargo is unloaded at final destination
    cargo.handled(new HandlingActivity(UNLOAD, NEWYORK, voyage), new Date());
    assertTrue(cargo.isReadyToClaim());
  }

  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);
    cargo.handled(new HandlingActivity(RECEIVE, STOCKHOLM), new Date());
    return cargo;
  }

  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

    cargo.handled(new HandlingActivity(CLAIM, MELBOURNE), new Date());
    return cargo;
  }

  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);
    
    cargo.handled(new HandlingActivity(LOAD, STOCKHOLM, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, HAMBURG, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(LOAD, HAMBURG, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, HONGKONG, crazyVoyage), new Date());
    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);
    
    cargo.handled(new HandlingActivity(LOAD, STOCKHOLM, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, HAMBURG, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(LOAD, HAMBURG, crazyVoyage), new Date());
    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    cargo.handled(new HandlingActivity(LOAD, STOCKHOLM, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, HAMBURG, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(LOAD, HAMBURG, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, HONGKONG, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(LOAD, HONGKONG, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, MELBOURNE, crazyVoyage), new Date());

    return cargo;
  }

  public void testIsMisdirected() throws Exception {
    //A cargo with no itinerary is not misdirected
    Cargo cargo = new Cargo(new TrackingId("TRKID"), new RouteSpecification(SHANGHAI, GOTHENBURG, new Date()));
    assertFalse(cargo.isMisdirected());

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    //A cargo with no handling events is not misdirected
    assertFalse(cargo.isMisdirected());

    //Happy path
    cargo.handled(new HandlingActivity(RECEIVE, SHANGHAI), new Date());
    cargo.handled(new HandlingActivity(LOAD, SHANGHAI, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, ROTTERDAM, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(LOAD, ROTTERDAM, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, GOTHENBURG, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(CLAIM, GOTHENBURG), new Date());
    cargo.handled(new HandlingActivity(CUSTOMS, GOTHENBURG), new Date());
    assertFalse(cargo.isMisdirected());

    //Try a couple of failing ones

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    cargo.handled(new HandlingActivity(RECEIVE, HANGZOU), new Date());
    assertTrue(cargo.isMisdirected());



    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    cargo.handled(new HandlingActivity(RECEIVE, SHANGHAI), new Date());
    cargo.handled(new HandlingActivity(LOAD, SHANGHAI, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(UNLOAD, ROTTERDAM, crazyVoyage), new Date());
    cargo.handled(new HandlingActivity(CLAIM, ROTTERDAM), new Date());

    assertTrue(cargo.isMisdirected());
  }

  public void testCustomsClearancePoint() {
    //cargo destination NYC
    final Cargo cargo = new Cargo(new TrackingId("XYZ"),
      new RouteSpecification(SHANGHAI, NEWYORK, new Date()));

    //SHA-LGB-NYC
    cargo.assignToRoute(new Itinerary(
      Leg.deriveLeg(pacific, SHANGHAI, LONGBEACH),
      Leg.deriveLeg(transcontinental, LONGBEACH, NEWYORK)));
    assertEquals(LONGBEACH, cargo.customsClearancePoint());

    //SHA-SEA-NYC
    cargo.assignToRoute(new Itinerary(
      Leg.deriveLeg(pacific, SHANGHAI, SEATTLE),
      Leg.deriveLeg(northernRail, SEATTLE, NEWYORK)));
    assertEquals(SEATTLE, cargo.customsClearancePoint());

    //cargo destination LGB
    //SHA-LGB
    cargo.specifyNewRoute(new RouteSpecification(SHANGHAI, LONGBEACH, new Date()));
    cargo.assignToRoute(new Itinerary(
      Leg.deriveLeg(pacific, SHANGHAI, LONGBEACH)));
    assertEquals(LONGBEACH, cargo.customsClearancePoint());

    //Cargo destination HAMBURG
    //SHA-LGB-NYC This itinerary does not take
    // the cargo into its CustomsZone, so no clearancePoint.
    cargo.specifyNewRoute(new RouteSpecification(SHANGHAI, HAMBURG, new Date()));
    cargo.assignToRoute(new Itinerary(
      Leg.deriveLeg(pacific, SHANGHAI, LONGBEACH),
      Leg.deriveLeg(transcontinental, LONGBEACH, NEWYORK)));
    assertNull(cargo.customsClearancePoint());

    //Cargo destination NEWYORK on SHA-LGB-CHI
    //This itinerary does not take the cargo to its destination,
    //but it does enter the CustomsZone, so it has a clearancePoint.
    cargo.specifyNewRoute(new RouteSpecification(SHANGHAI, NEWYORK, new Date()));
    cargo.assignToRoute(new Itinerary(
      Leg.deriveLeg(pacific, SHANGHAI, LONGBEACH),
      Leg.deriveLeg(transcontinental, LONGBEACH, CHICAGO)));
    assertEquals(LONGBEACH, cargo.customsClearancePoint());

  }

  private Cargo setUpCargoWithItinerary(Location origin, Location midpoint, Location destination) {
    Cargo cargo = new Cargo(new TrackingId("CARGO1"), new RouteSpecification(origin, destination, new Date()));

    Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(crazyVoyage, origin, midpoint, new Date(), new Date()),
        new Leg(crazyVoyage, midpoint, destination, new Date(), new Date())
      )
    );

    cargo.assignToRoute(itinerary);
    return cargo;
  }

}
