package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.NOT_RECEIVED;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    assertEquals(NOT_ROUTED, cargo.delivery().routingStatus());
    assertEquals(NOT_RECEIVED, cargo.delivery().transportStatus());
    assertEquals(Location.UNKNOWN, cargo.delivery().lastKnownLocation());
    assertEquals(Voyage.NONE, cargo.delivery().currentVoyage());
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

    assertEquals(NOT_ROUTED, cargo.delivery().routingStatus());

    cargo.assignToRoute(bad);
    assertEquals(MISROUTED, cargo.delivery().routingStatus());

    cargo.assignToRoute(good);
    assertEquals(ROUTED, cargo.delivery().routingStatus());
  }

  public void testlastKnownLocationUnknownWhenNoEvents() throws Exception {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    assertEquals(Location.UNKNOWN, cargo.delivery().lastKnownLocation());
  }

  public void testlastKnownLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

    assertEquals(STOCKHOLM, cargo.delivery().lastKnownLocation());
  }

  public void testlastKnownLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

    assertEquals(MELBOURNE, cargo.delivery().lastKnownLocation());
  }

  public void testlastKnownLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

    assertEquals(HONGKONG, cargo.delivery().lastKnownLocation());
  }

  public void testlastKnownLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

    assertEquals(HAMBURG, cargo.delivery().lastKnownLocation());
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
    assertFalse(cargo.delivery().isUnloadedAtDestination());

    // Adding an event unrelated to unloading at final destination

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(
      new HandlingEvent(cargo, new Date(10), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU));
    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    assertFalse(cargo.delivery().isUnloadedAtDestination());

    Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"), HANGZOU).
      addMovement(NEWYORK, new Date(), new Date()).
      build();

    // Adding an unload event, but not at the final destination
    events.add(
      new HandlingEvent(cargo, new Date(20), new Date(), HandlingEvent.Type.UNLOAD, TOKYO, voyage));
    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    assertFalse(cargo.delivery().isUnloadedAtDestination());

    // Adding an event in the final destination, but not unload
    events.add(
      new HandlingEvent(cargo, new Date(30), new Date(), HandlingEvent.Type.CUSTOMS, NEWYORK));
    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    assertFalse(cargo.delivery().isUnloadedAtDestination());

    // Finally, cargo is unloaded at final destination
    events.add(
      new HandlingEvent(cargo, new Date(40), new Date(), HandlingEvent.Type.UNLOAD, NEWYORK, voyage));
    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    assertTrue(cargo.delivery().isUnloadedAtDestination());
  }

  // TODO: Generate test data some better way
  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    HandlingEvent he = new HandlingEvent(cargo, toDate("2007-12-01"), new Date(), HandlingEvent.Type.RECEIVE, STOCKHOLM);
    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(he);
    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));

    return cargo;
  }

  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();
    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, toDate("2007-12-09"), new Date(), HandlingEvent.Type.CLAIM, MELBOURNE));
    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));

    return cargo;
  }

  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, toDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, crazyVoyage));

    events.add(new HandlingEvent(cargo, toDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, crazyVoyage));

    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, toDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, crazyVoyage));

    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, toDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, crazyVoyage));

    events.add(new HandlingEvent(cargo, toDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, crazyVoyage));

    events.add(new HandlingEvent(cargo, toDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, HONGKONG, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-07"), new Date(), HandlingEvent.Type.UNLOAD, MELBOURNE, crazyVoyage));

    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    return cargo;
  }

  private Cargo populateCargoOnHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, toDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, crazyVoyage));

    events.add(new HandlingEvent(cargo, toDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, crazyVoyage));
    events.add(new HandlingEvent(cargo, toDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, crazyVoyage));

    events.add(new HandlingEvent(cargo, toDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, HONGKONG, crazyVoyage));

    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    return cargo;
  }

  public void testIsMisdirected() throws Exception {
    //A cargo with no itinerary is not misdirected
    Cargo cargo = new Cargo(new TrackingId("TRKID"), new RouteSpecification(SHANGHAI, GOTHENBURG, new Date()));
    assertFalse(cargo.delivery().isMisdirected());

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    //A cargo with no handling events is not misdirected
    assertFalse(cargo.delivery().isMisdirected());

    //Happy path
    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    events.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, crazyVoyage));
    events.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, crazyVoyage));
    events.add(new HandlingEvent(cargo, new Date(70), new Date(80), HandlingEvent.Type.LOAD, ROTTERDAM, crazyVoyage));
    events.add(new HandlingEvent(cargo, new Date(90), new Date(100), HandlingEvent.Type.UNLOAD, GOTHENBURG, crazyVoyage));
    events.add(new HandlingEvent(cargo, new Date(110), new Date(120), HandlingEvent.Type.CLAIM, GOTHENBURG));
    events.add(new HandlingEvent(cargo, new Date(130), new Date(140), HandlingEvent.Type.CUSTOMS, GOTHENBURG));

    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));
    assertFalse(cargo.delivery().isMisdirected());

    //Try a couple of failing ones

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    events.add(new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU));
    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));

    assertTrue(cargo.delivery().isMisdirected());

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    events.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    events.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, crazyVoyage));
    events.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, crazyVoyage));
    events.add(new HandlingEvent(cargo, new Date(70), new Date(80), HandlingEvent.Type.LOAD, ROTTERDAM, crazyVoyage));

    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));

    assertTrue(cargo.delivery().isMisdirected());

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    events.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    events.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, crazyVoyage));
    events.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, crazyVoyage));
    events.add(new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, ROTTERDAM));

    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));

    assertTrue(cargo.delivery().isMisdirected());
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
