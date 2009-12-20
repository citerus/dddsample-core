package se.citerus.dddsample.tracking.core.domain.model.cargo;

import junit.framework.TestCase;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.*;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;

import java.util.Date;

public class CargoTest extends TestCase {

  private Voyage crazyVoyage = new Voyage.Builder(new VoyageNumber("0123"),
      STOCKHOLM).
      addMovement(HAMBURG, new Date(1), new Date(2)).
      addMovement(HONGKONG, new Date(3), new Date(4)).
      addMovement(MELBOURNE, new Date(5), new Date(6)).
      build();

  private Voyage pacific = new Voyage.Builder(new VoyageNumber("4567"),
      SHANGHAI).
      addMovement(LONGBEACH, new Date(1), new Date(2)).
      addMovement(SEATTLE, new Date(3), new Date(4)).
      build();

  private Voyage transcontinental = new Voyage.Builder(new VoyageNumber("4567"),
      LONGBEACH).
      addMovement(CHICAGO, new Date(1), new Date(2)).
      addMovement(NEWYORK, new Date(3), new Date(4)).
      build();

  private Voyage northernRail = new Voyage.Builder(new VoyageNumber("8901"),
      SEATTLE).
      addMovement(CHICAGO, new Date(1), new Date(2)).
      addMovement(NEWYORK, new Date(3), new Date(4)).
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
    assertEquals(Location.NONE, cargo.lastKnownLocation());
    assertEquals(Voyage.NONE, cargo.currentVoyage());
  }

  public void testEmptyCtor() {
    new Cargo();
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
    Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    cargo.handled(loadOnto(crazyVoyage).in(STOCKHOLM));
    assertThat(cargo.transportStatus(), is(ONBOARD_CARRIER));
    assertThat(cargo.lastKnownLocation(), is(STOCKHOLM));

    cargo.handled(unloadOff(crazyVoyage).in(HAMBURG));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertThat(cargo.lastKnownLocation(), is(HAMBURG));

    cargo.handled(unloadOff(crazyVoyage).in(MELBOURNE));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertThat(cargo.lastKnownLocation(), is(MELBOURNE));

    // Out of order handling, does not affect state of cargo
    cargo.handled(loadOnto(crazyVoyage).in(HAMBURG));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertThat(cargo.lastKnownLocation(), is(MELBOURNE));
  }

  public void testlastKnownLocationUnknownWhenNoEvents() throws Exception {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    assertEquals(Location.NONE, cargo.lastKnownLocation());
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

    assertEquals(MELBOURNE, cargo.lastKnownLocation());
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

  public void testIsReadyToClaimWithDestinationDifferentFromCustomsClearancePoint() {
    Cargo cargo = new Cargo(new TrackingId("CARGO1"), new RouteSpecification(HONGKONG, NEWYORK, toDate("2009-12-24")));
    Itinerary itinerary = new Itinerary(
      Leg.deriveLeg(pacific1, HONGKONG, LONGBEACH),
      Leg.deriveLeg(continental2, LONGBEACH, NEWYORK)
    );
    cargo.assignToRoute(itinerary);
    assertFalse(cargo.routeSpecification().destination().sameAs(cargo.customsClearancePoint()));
    assertFalse(cargo.isReadyToClaim());

    cargo.handled(unloadOff(pacific1).in(LONGBEACH));
    assertFalse(cargo.isReadyToClaim());

    cargo.handled(loadOnto(continental2).in(LONGBEACH));
    assertFalse(cargo.isReadyToClaim());
    
    cargo.handled(unloadOff(continental2).in(NEWYORK));
    assertTrue(cargo.isReadyToClaim());
  }

  public void testIsReadyToClaimWithDestinationSameAsCustomsClearancePoint() {
    Cargo cargo = new Cargo(new TrackingId("CARGO1"), new RouteSpecification(SHANGHAI, SEATTLE, toDate("2009-12-24")));
    Itinerary itinerary = new Itinerary(
      Leg.deriveLeg(pacific2, SHANGHAI, SEATTLE)
    );
    cargo.assignToRoute(itinerary);
    assertTrue(cargo.routeSpecification().destination().sameAs(cargo.customsClearancePoint()));
    assertFalse(cargo.isReadyToClaim());

    cargo.handled(unloadOff(pacific2).in(SEATTLE));
    assertFalse(cargo.isReadyToClaim());

    cargo.handled(customsIn(SEATTLE));
    assertTrue(cargo.isReadyToClaim());

    cargo.handled(claimIn(SEATTLE));
    assertFalse(cargo.isReadyToClaim());
  }

  public void testIsMisdirectedHappyPath() throws Exception {
    Cargo cargo = shanghaiSeattleChicagoOnPacific2AndContinental3();

    //A cargo with no handling events is not misdirected
    assertFalse(cargo.isMisdirected());

    cargo.handled(receiveIn(SHANGHAI));
    assertFalse(cargo.isMisdirected());

    cargo.handled(loadOnto(pacific2).in(SHANGHAI));
    assertFalse(cargo.isMisdirected());

    cargo.handled(unloadOff(pacific2).in(SEATTLE));
    assertFalse(cargo.isMisdirected());

    cargo.handled(customsIn(SEATTLE));
    assertFalse(cargo.isMisdirected());

    cargo.handled(loadOnto(continental3).in(SEATTLE));
    assertFalse(cargo.isMisdirected());

    cargo.handled(unloadOff(continental3).in(CHICAGO));
    assertFalse(cargo.isMisdirected());

    cargo.handled(claimIn(CHICAGO));
    assertFalse(cargo.isMisdirected());
  }

  public void testIsMisdirectedIncorrectReceive() throws Exception {
    Cargo cargo = shanghaiSeattleChicagoOnPacific2AndContinental3();

    cargo.handled(receiveIn(TOKYO));
    assertTrue(cargo.isMisdirected());
  }

  public void testIsMisdirectedLoadOntoWrongVoyage() throws Exception {
    Cargo cargo = shanghaiSeattleChicagoOnPacific2AndContinental3();

    cargo.handled(loadOnto(pacific1).in(HONGKONG));
    assertTrue(cargo.isMisdirected());
  }

  public void testIsMisdirectedUnloadInWrongLocation() throws Exception {
    Cargo cargo = shanghaiSeattleChicagoOnPacific2AndContinental3();

    cargo.handled(unloadOff(pacific2).in(TOKYO));
    assertTrue(cargo.isMisdirected());
  }

  public void testIsMisdirectedCustomsInWrongLocation() throws Exception {
    Cargo cargo = shanghaiSeattleChicagoOnPacific2AndContinental3();

    cargo.handled(customsIn(CHICAGO));
    assertTrue(cargo.isMisdirected());
  }

  public void testIsMisdirectedClaimedInWrongLocation() throws Exception {
    Cargo cargo = shanghaiSeattleChicagoOnPacific2AndContinental3();

    cargo.handled(claimIn(SEATTLE));
    assertTrue(cargo.isMisdirected());
  }

  public void testIsMisdirectedAfterRerouting() throws Exception {
    Cargo cargo = shanghaiSeattleChicagoOnPacific2AndContinental3();

    cargo.handled(loadOnto(pacific2).in(SHANGHAI));
    assertFalse(cargo.isMisdirected());

    // Cargo destination is changed by customer mid-route
    RouteSpecification newRouteSpec = cargo.routeSpecification().
      withOrigin(cargo.lastKnownLocation()).
      withDestination(NEWYORK);

    cargo.specifyNewRoute(newRouteSpec);
    // Misrouted, but not misdirected. Delivery is still accoring to plan (itinerary),
    // but not according to desire (route specification).
    assertFalse(cargo.isMisdirected());
    assertTrue(cargo.routingStatus() == MISROUTED);

    /**
     * This is a perfect example of how LegMatch is a modelling breakthrough.
     * It allows us to easily construct an itinerary that completes the remainder of the
     * old itinerary and appends the new and different path.
     */
    Leg currentLeg = cargo.itinerary().legMatchOf(cargo.mostRecentHandlingActivity()).leg();
    Itinerary newItinerary = new Itinerary(
      currentLeg,
      Leg.deriveLeg(continental3, SEATTLE, NEWYORK)
    );
    cargo.assignToRoute(newItinerary);
    assertFalse(cargo.isMisdirected());

    cargo.handled(unloadOff(pacific2).in(SEATTLE));
    assertFalse(cargo.isMisdirected());

    cargo.handled(loadOnto(continental3).in(SEATTLE));
    assertFalse(cargo.isMisdirected());

    cargo.handled(unloadOff(continental3).in(NEWYORK));
    assertFalse(cargo.isMisdirected());
  }

  public void testCustomsClearancePoint() {
    //cargo destination NYC
    final Cargo cargo = new Cargo(new TrackingId("XYZ"),
        new RouteSpecification(SHANGHAI, NEWYORK, new Date()));

    assertThat(cargo.customsClearancePoint(), is(Location.NONE));

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

  private Cargo shanghaiSeattleChicagoOnPacific2AndContinental3() {
    Cargo cargo = new Cargo(new TrackingId("CARGO1"), new RouteSpecification(SHANGHAI, CHICAGO, toDate("2009-12-24")));

    // A cargo with no itinerary is not misdirected
    assertFalse(cargo.isMisdirected());

    Itinerary itinerary = new Itinerary(
        Leg.deriveLeg(pacific2, SHANGHAI, SEATTLE),
        Leg.deriveLeg(continental3, SEATTLE, CHICAGO)
    );
    cargo.assignToRoute(itinerary);
    return cargo;
  }

  private Cargo setUpCargoWithItinerary(Location origin, Location midpoint, Location destination) {
    Cargo cargo = new Cargo(new TrackingId("CARGO1"), new RouteSpecification(origin, destination, new Date()));

    Itinerary itinerary = new Itinerary(
        Leg.deriveLeg(crazyVoyage, origin, midpoint),
        Leg.deriveLeg(crazyVoyage, midpoint, destination)
    );

    cargo.assignToRoute(itinerary);
    return cargo;
  }

  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);
    cargo.handled(new HandlingActivity(RECEIVE, STOCKHOLM));
    return cargo;
  }

  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

    cargo.handled(new HandlingActivity(CLAIM, MELBOURNE));
    return cargo;
  }

  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    cargo.handled(new HandlingActivity(LOAD, STOCKHOLM, crazyVoyage));
    cargo.handled(new HandlingActivity(UNLOAD, HAMBURG, crazyVoyage));
    cargo.handled(new HandlingActivity(LOAD, HAMBURG, crazyVoyage));
    cargo.handled(new HandlingActivity(UNLOAD, MELBOURNE, crazyVoyage));
    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    cargo.handled(new HandlingActivity(LOAD, STOCKHOLM, crazyVoyage));
    cargo.handled(new HandlingActivity(UNLOAD, HAMBURG, crazyVoyage));
    cargo.handled(new HandlingActivity(LOAD, HAMBURG, crazyVoyage));
    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = setUpCargoWithItinerary(STOCKHOLM, HAMBURG, MELBOURNE);

    cargo.handled(new HandlingActivity(LOAD, STOCKHOLM, crazyVoyage));
    cargo.handled(new HandlingActivity(UNLOAD, HAMBURG, crazyVoyage));
    cargo.handled(new HandlingActivity(LOAD, HAMBURG, crazyVoyage));
    cargo.handled(new HandlingActivity(UNLOAD, MELBOURNE, crazyVoyage));

    return cargo;
  }


}
