package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.MISROUTED;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.ROUTED;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.domain.model.location.Location;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.shared.HandlingActivity;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.*;
import se.citerus.dddsample.domain.model.voyage.Voyage;

import java.util.Date;

public class DeliveryTest extends TestCase {

  Delivery delivery;
  Projections projections;
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
    projections = new Projections(delivery, itinerary, routeSpecification);
    Thread.sleep(1);
  }

  public void testDerivedFromRouteSpecificationAndItinerary() throws Exception {
    assertEquals(ROUTED, delivery.routingStatus(itinerary, routeSpecification));
    assertEquals(Voyage.NONE, delivery.currentVoyage());
    assertFalse(delivery.isMisdirected(itinerary, routeSpecification));
    assertFalse(delivery.isUnloadedAtDestination(routeSpecification));
    assertEquals(Location.UNKNOWN, delivery.lastKnownLocation());
    assertEquals(NOT_RECEIVED, delivery.transportStatus());
    assertTrue(delivery.calculatedAt().before(new Date()));

    assertEquals(new HandlingActivity(RECEIVE, HANGZOU), projections.nextExpectedActivity());
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), projections.estimatedTimeOfArrival());
  }

  public void testUpdateOnHandlingHappyPath() {
    // 1. Receive

    HandlingActivity handlingActivity = new HandlingActivity(RECEIVE, HANGZOU);
    Delivery newDelivery = Delivery.whenHandled(handlingActivity);
    Projections newProjections = new Projections(newDelivery, itinerary, routeSpecification);

    // Changed on handling
    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(HANGZOU, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    // Changed on handling and/or (re-)routing
    assertEquals(new HandlingActivity(LOAD, HANGZOU, HONGKONG_TO_NEW_YORK), newProjections.nextExpectedActivity());
    assertFalse(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertFalse(newDelivery.isUnloadedAtDestination(routeSpecification));

    // Changed on (re-)routing
    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), newProjections.estimatedTimeOfArrival());

    // Updated on every calculation
    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));

    // 2. Load

    handlingActivity = new HandlingActivity(LOAD, HANGZOU, HONGKONG_TO_NEW_YORK);
    newDelivery = Delivery.whenHandled(handlingActivity);
    newProjections = new Projections(newDelivery, itinerary, routeSpecification);

    assertEquals(HONGKONG_TO_NEW_YORK, newDelivery.currentVoyage());
    assertEquals(HANGZOU, newDelivery.lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, newDelivery.transportStatus());

    assertEquals(new HandlingActivity(UNLOAD, NEWYORK, HONGKONG_TO_NEW_YORK), newProjections.nextExpectedActivity());
    assertFalse(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertFalse(newDelivery.isUnloadedAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), newProjections.estimatedTimeOfArrival());

    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));

    // Skipping intermediate load/unloads

    // 3. Unload

    handlingActivity = new HandlingActivity(UNLOAD, STOCKHOLM, DALLAS_TO_HELSINKI);
    newDelivery = Delivery.whenHandled(handlingActivity);
    newProjections = new Projections(newDelivery, itinerary, routeSpecification);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(STOCKHOLM, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    assertEquals(new HandlingActivity(CLAIM, STOCKHOLM), newProjections.nextExpectedActivity());
    assertFalse(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertTrue(newDelivery.isUnloadedAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), newProjections.estimatedTimeOfArrival());

    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));

    // 4. Claim

    handlingActivity = new HandlingActivity(CLAIM, STOCKHOLM);
    newDelivery = Delivery.whenHandled(handlingActivity);
    newProjections = new Projections(newDelivery, itinerary, routeSpecification);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(STOCKHOLM, newDelivery.lastKnownLocation());
    assertEquals(CLAIMED, newDelivery.transportStatus());

    assertNull(newProjections.nextExpectedActivity());
    assertFalse(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertTrue(newDelivery.isUnloadedAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), newProjections.estimatedTimeOfArrival());

    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));
  }

  public void testUpdateOnHandlingWhenMisdirected() {
    // Unload in Hamburg, which is the wrong location
    HandlingActivity handlingActivity = new HandlingActivity(UNLOAD, HAMBURG, DALLAS_TO_HELSINKI);
    Delivery newDelivery = Delivery.whenHandled(handlingActivity);
    Projections newProjections = new Projections(newDelivery, itinerary, routeSpecification);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(HAMBURG, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    // Next handling activity is undefined. Need a new itinerary to know what to do.
    assertNull(newProjections.nextExpectedActivity());
    
    assertTrue(newDelivery.isMisdirected(itinerary, routeSpecification));
    assertFalse(newDelivery.isUnloadedAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    // ETA is undefined at this time
    assertNull(newProjections.estimatedTimeOfArrival());

    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));

    // New route specification, old itinerary
    RouteSpecification newRouteSpecification = routeSpecification.withOrigin(HAMBURG);
    newProjections = new Projections(newDelivery, itinerary, newRouteSpecification);
    assertEquals(MISROUTED, newDelivery.routingStatus(itinerary, newRouteSpecification));

    // TODO is it really misdirected at this point?
    assertTrue(newDelivery.isMisdirected(itinerary, newRouteSpecification));

    assertNull(newProjections.nextExpectedActivity());

    Itinerary newItinerary = new Itinerary(
      Leg.deriveLeg(DALLAS_TO_HELSINKI, HAMBURG, STOCKHOLM)
    );

    newProjections = new Projections(newDelivery, newItinerary, newRouteSpecification);

    assertEquals(ROUTED, newDelivery.routingStatus(newItinerary, newRouteSpecification));
    // TODO is it really misdirected here?
    assertTrue(newDelivery.isMisdirected(newItinerary, newRouteSpecification));
    assertEquals(IN_PORT, newDelivery.transportStatus());
    //assertEquals(new HandlingActivity(LOAD, HAMBURG, DALLAS_TO_HELSINKI), newProjections.nextExpectedActivity());
    assertNull(newProjections.nextExpectedActivity());
  }

}
