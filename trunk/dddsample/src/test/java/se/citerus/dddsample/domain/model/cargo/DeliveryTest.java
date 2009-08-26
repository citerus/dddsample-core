package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.application.util.DateTestUtil.*;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.location.SampleLocations.DALLAS;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.*;
import se.citerus.dddsample.domain.model.shared.HandlingActivity;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.*;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;

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
    delivery = Delivery.initial(routeSpecification, itinerary);
    projections = new Projections(delivery, itinerary, routeSpecification);
    Thread.sleep(1);
  }

  public void testDerivedFromRouteSpecificationAndItinerary() throws Exception {
    assertEquals(ROUTED, delivery.routingStatus());
    assertEquals(Voyage.NONE, delivery.currentVoyage());
    assertFalse(delivery.isMisdirected());
    assertFalse(delivery.isUnloadedAtDestination());
    assertEquals(Location.UNKNOWN, delivery.lastKnownLocation());
    assertEquals(NOT_RECEIVED, delivery.transportStatus());
    assertTrue(delivery.calculatedAt().before(new Date()));

    assertEquals(new HandlingActivity(RECEIVE, HANGZOU), projections.nextExpectedActivity());
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), projections.estimatedTimeOfArrival());
  }

  public void testUpdateOnHandlingHappyPath() {
    // 1. Receive

    HandlingActivity handlingActivity = new HandlingActivity(RECEIVE, HANGZOU);
    Delivery newDelivery = delivery.whenHandled(routeSpecification, itinerary, handlingActivity);
    Projections newProjections = new Projections(newDelivery, itinerary, routeSpecification, handlingActivity);

    // Changed on handling
    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(HANGZOU, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    // Changed on handling and/or (re-)routing
    assertEquals(new HandlingActivity(LOAD, HANGZOU, HONGKONG_TO_NEW_YORK), newProjections.nextExpectedActivity());
    assertFalse(newDelivery.isMisdirected());
    assertFalse(newDelivery.isUnloadedAtDestination());

    // Changed on (re-)routing
    assertEquals(ROUTED, newDelivery.routingStatus());
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), newProjections.estimatedTimeOfArrival());

    // Updated on every calculation
    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));

    // 2. Load

    handlingActivity = new HandlingActivity(LOAD, HANGZOU, HONGKONG_TO_NEW_YORK);
    newDelivery = newDelivery.whenHandled(routeSpecification, itinerary, handlingActivity);
    newProjections = new Projections(newDelivery, itinerary, routeSpecification, handlingActivity);

    assertEquals(HONGKONG_TO_NEW_YORK, newDelivery.currentVoyage());
    assertEquals(HANGZOU, newDelivery.lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, newDelivery.transportStatus());

    assertEquals(new HandlingActivity(UNLOAD, NEWYORK, HONGKONG_TO_NEW_YORK), newProjections.nextExpectedActivity());
    assertFalse(newDelivery.isMisdirected());
    assertFalse(newDelivery.isUnloadedAtDestination());

    assertEquals(ROUTED, newDelivery.routingStatus());
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), newProjections.estimatedTimeOfArrival());

    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));

    // Skipping intermediate load/unloads

    // 3. Unload

    handlingActivity = new HandlingActivity(UNLOAD, STOCKHOLM, DALLAS_TO_HELSINKI);
    newDelivery = newDelivery.whenHandled(routeSpecification, itinerary, handlingActivity);
    newProjections = new Projections(newDelivery, itinerary, routeSpecification, handlingActivity);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(STOCKHOLM, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    assertEquals(new HandlingActivity(CLAIM, STOCKHOLM), newProjections.nextExpectedActivity());
    assertFalse(newDelivery.isMisdirected());
    assertTrue(newDelivery.isUnloadedAtDestination());

    assertEquals(ROUTED, newDelivery.routingStatus());
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), newProjections.estimatedTimeOfArrival());

    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));

    // 4. Claim

    handlingActivity = new HandlingActivity(CLAIM, STOCKHOLM);
    newDelivery = newDelivery.whenHandled(routeSpecification, itinerary, handlingActivity);
    newProjections = new Projections(newDelivery, itinerary, routeSpecification, handlingActivity);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(STOCKHOLM, newDelivery.lastKnownLocation());
    assertEquals(CLAIMED, newDelivery.transportStatus());

    assertNull(newProjections.nextExpectedActivity());
    assertFalse(newDelivery.isMisdirected());
    assertTrue(newDelivery.isUnloadedAtDestination());

    assertEquals(ROUTED, newDelivery.routingStatus());
    assertEquals(DALLAS_TO_HELSINKI.schedule().arrivalTimeAt(STOCKHOLM), newProjections.estimatedTimeOfArrival());

    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));
  }

  public void testUpdateOnHandlingWhenMisdirected() {
    // Unload in Hamburg, which is the wrong location
    HandlingActivity handlingActivity = new HandlingActivity(UNLOAD, HAMBURG, DALLAS_TO_HELSINKI);
    Delivery newDelivery = delivery.whenHandled(routeSpecification, itinerary, handlingActivity);
    Projections newProjections = new Projections(newDelivery, itinerary, routeSpecification, handlingActivity);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(HAMBURG, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    // Next handling activity is undefined. Need a new itinerary to know what to do.
    assertNull(newProjections.nextExpectedActivity());
    
    assertTrue(newDelivery.isMisdirected());
    assertFalse(newDelivery.isUnloadedAtDestination());

    assertEquals(ROUTED, newDelivery.routingStatus());

    // ETA is undefined at this time
    assertNull(newProjections.estimatedTimeOfArrival());

    assertTrue(delivery.calculatedAt().before(newDelivery.calculatedAt()));

    // New route specification, old itinerary
    RouteSpecification newRouteSpecification = routeSpecification.withOrigin(HAMBURG);
    newDelivery = newDelivery.withRoutingChange(newRouteSpecification, itinerary);
    newProjections = new Projections(newDelivery, itinerary, newRouteSpecification);
    assertEquals(MISROUTED, newDelivery.routingStatus());

    // TODO is it misdirected at this point?
    //assertTrue(newDelivery.isMisdirected());
    assertFalse(newDelivery.isMisdirected());

    assertNull(newProjections.nextExpectedActivity());

    Itinerary newItinerary = new Itinerary(
      Leg.deriveLeg(DALLAS_TO_HELSINKI, HAMBURG, STOCKHOLM)
    );

    newDelivery = newDelivery.withRoutingChange(newRouteSpecification, newItinerary);
    newProjections = new Projections(newDelivery, newItinerary, newRouteSpecification);

    assertEquals(ROUTED, newDelivery.routingStatus());
    assertFalse(newDelivery.isMisdirected());
    assertEquals(IN_PORT, newDelivery.transportStatus());
    assertEquals(new HandlingActivity(LOAD, HAMBURG, DALLAS_TO_HELSINKI), newProjections.nextExpectedActivity());
  }

}
