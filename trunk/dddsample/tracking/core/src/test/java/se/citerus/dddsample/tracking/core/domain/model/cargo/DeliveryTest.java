package se.citerus.dddsample.tracking.core.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.MISROUTED;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.ROUTED;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.*;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;

import java.util.Date;

public class DeliveryTest extends TestCase {

  Delivery delivery;
  Itinerary itinerary;
  RouteSpecification routeSpecification;

  @Override
  protected void setUp() throws Exception {
    routeSpecification = new RouteSpecification(HANGZOU, STOCKHOLM, toDate("2008-11-03"));
    itinerary = new Itinerary(
      Leg.deriveLeg(HONGKONG_TO_NEW_YORK, HANGZOU, NEWYORK),
      Leg.deriveLeg(NEW_YORK_TO_DALLAS, NEWYORK, DALLAS),
      Leg.deriveLeg(DALLAS_TO_HELSINKI, DALLAS, STOCKHOLM)
    );
    delivery = Delivery.initial();
    Thread.sleep(1);
  }

  public void testDerivedFromRouteSpecificationAndItinerary() throws Exception {
    assertEquals(ROUTED, delivery.routingStatus(itinerary, routeSpecification));
    assertEquals(Voyage.NONE, delivery.currentVoyage());
    assertFalse(delivery.isMisdirected(itinerary, routeSpecification));
    assertFalse(delivery.isUnloadedAtDestination(routeSpecification));
    assertEquals(Location.UNKNOWN, delivery.lastKnownLocation());
    assertEquals(NOT_RECEIVED, delivery.transportStatus());
    assertTrue(delivery.lastTimestamp().before(new Date()));
  }

  public void testUpdateOnHandlingHappyPath() {
    // 1. Receive

    HandlingActivity handlingActivity = new HandlingActivity(RECEIVE, HANGZOU);
    Delivery newDelivery = Delivery.cargoWasHandled(handlingActivity, new Date());

    // Changed on handling
    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(HANGZOU, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    // Changed on handling and/or (re-)routing
    assertFalse(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertFalse(newDelivery.isUnloadedAtDestination(routeSpecification));

    // Changed on (re-)routing
    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    // Updated on every calculation
    assertTrue(delivery.lastTimestamp().before(newDelivery.lastTimestamp()));

    // 2. Load

    handlingActivity = new HandlingActivity(LOAD, HANGZOU, HONGKONG_TO_NEW_YORK);
    newDelivery = Delivery.cargoWasHandled(handlingActivity, new Date());

    assertEquals(HONGKONG_TO_NEW_YORK, newDelivery.currentVoyage());
    assertEquals(HANGZOU, newDelivery.lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, newDelivery.transportStatus());

    assertFalse(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertFalse(newDelivery.isUnloadedAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    assertTrue(delivery.lastTimestamp().before(newDelivery.lastTimestamp()));

    // Skipping intermediate load/unloads

    // 3. Unload

    handlingActivity = new HandlingActivity(UNLOAD, STOCKHOLM, DALLAS_TO_HELSINKI);
    newDelivery = Delivery.cargoWasHandled(handlingActivity, new Date());

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(STOCKHOLM, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    assertFalse(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertTrue(newDelivery.isUnloadedAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    assertTrue(delivery.lastTimestamp().before(newDelivery.lastTimestamp()));

    // 4. Claim

    handlingActivity = new HandlingActivity(CLAIM, STOCKHOLM);
    newDelivery = Delivery.cargoWasHandled(handlingActivity, new Date());

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(STOCKHOLM, newDelivery.lastKnownLocation());
    assertEquals(CLAIMED, newDelivery.transportStatus());

    assertFalse(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertTrue(newDelivery.isUnloadedAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    assertTrue(delivery.lastTimestamp().before(newDelivery.lastTimestamp()));
  }

  public void testUpdateOnHandlingWhenMisdirected() {
    // Unload in Hamburg, which is the wrong location
    HandlingActivity handlingActivity = new HandlingActivity(UNLOAD, HAMBURG, DALLAS_TO_HELSINKI);
    Delivery newDelivery = Delivery.cargoWasHandled(handlingActivity, new Date());

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(HAMBURG, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    // Next handling activity is undefined. Need a new itinerary to know what to do.

    assertTrue(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertFalse(newDelivery.isUnloadedAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    assertTrue(delivery.lastTimestamp().before(newDelivery.lastTimestamp()));

    // New route specification, old itinerary
    RouteSpecification newRouteSpecification = routeSpecification.withOrigin(HAMBURG);
    assertEquals(MISROUTED, newDelivery.routingStatus(itinerary, newRouteSpecification));

    // TODO is it really misdirected at this point?
    assertTrue(newDelivery.isMisdirected(itinerary, newRouteSpecification));

    Itinerary newItinerary = new Itinerary(
      Leg.deriveLeg(DALLAS_TO_HELSINKI, HAMBURG, STOCKHOLM)
    );

    assertEquals(ROUTED, newDelivery.routingStatus(newItinerary, newRouteSpecification));
    // TODO is it really misdirected here?
    assertTrue(newDelivery.isMisdirected(newItinerary, newRouteSpecification));
    assertEquals(IN_PORT, newDelivery.transportStatus());
  }

}
