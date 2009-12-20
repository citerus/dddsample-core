package se.citerus.dddsample.tracking.core.domain.model.cargo;

import junit.framework.TestCase;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.MISROUTED;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.ROUTED;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.customsIn;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.loadOnto;
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
    delivery = Delivery.beforeHandling();
    Thread.sleep(1);
  }

  public void testOnHandling() {
    Delivery delivery = Delivery.beforeHandling();

    HandlingActivity load = loadOnto(HONGKONG_TO_NEW_YORK).in(HONGKONG);
    delivery = delivery.onHandling(load);

    assertThat(delivery.mostRecentHandlingActivity(), is(load));
    assertThat(delivery.mostRecentPhysicalHandlingActivity(), is(load));

    HandlingActivity customs = customsIn(NEWYORK);
    delivery = delivery.onHandling(customs);

    assertThat(delivery.mostRecentHandlingActivity(), is(customs));
    assertThat(delivery.mostRecentPhysicalHandlingActivity(), is(load));

    HandlingActivity loadAgain = loadOnto(NEW_YORK_TO_DALLAS).in(NEWYORK);
    delivery = delivery.onHandling(loadAgain);

    assertThat(delivery.mostRecentHandlingActivity(), is(loadAgain));
    assertThat(delivery.mostRecentPhysicalHandlingActivity(), is(loadAgain));
  }

  public void testDerivedFromRouteSpecificationAndItinerary() throws Exception {
    assertEquals(ROUTED, delivery.routingStatus(itinerary, routeSpecification));
    assertEquals(Voyage.NONE, delivery.currentVoyage());
    assertFalse(delivery.onTheGroundAtDestination(routeSpecification));
    assertEquals(Location.NONE, delivery.lastKnownLocation());
    assertEquals(NOT_RECEIVED, delivery.transportStatus());
    assertTrue(delivery.lastUpdatedOn().before(new Date()));
  }

  public void testUpdateOnHandlingHappyPath() {
    // 1. Receive

    HandlingActivity handlingActivity = new HandlingActivity(RECEIVE, HANGZOU);
    Delivery newDelivery = delivery.onHandling(handlingActivity);

    // Changed on handling
    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(HANGZOU, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    // Changed on handling and/or (re-)routing
    assertFalse(newDelivery.onTheGroundAtDestination(routeSpecification));

    // Changed on (re-)routing
    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    // Updated on every calculation
    assertTrue(delivery.lastUpdatedOn().before(newDelivery.lastUpdatedOn()));

    // 2. Load

    handlingActivity = new HandlingActivity(LOAD, HANGZOU, HONGKONG_TO_NEW_YORK);
    newDelivery = newDelivery.onHandling(handlingActivity);

    assertEquals(HONGKONG_TO_NEW_YORK, newDelivery.currentVoyage());
    assertEquals(HANGZOU, newDelivery.lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, newDelivery.transportStatus());

    assertFalse(newDelivery.onTheGroundAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    assertTrue(delivery.lastUpdatedOn().before(newDelivery.lastUpdatedOn()));

    // Skipping intermediate load/unloads

    // 3. Unload

    handlingActivity = new HandlingActivity(UNLOAD, STOCKHOLM, DALLAS_TO_HELSINKI);
    newDelivery = newDelivery.onHandling(handlingActivity);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(STOCKHOLM, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    assertTrue(newDelivery.onTheGroundAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    assertTrue(delivery.lastUpdatedOn().before(newDelivery.lastUpdatedOn()));

    // 4. Claim

    handlingActivity = new HandlingActivity(CLAIM, STOCKHOLM);
    newDelivery = newDelivery.onHandling(handlingActivity);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(STOCKHOLM, newDelivery.lastKnownLocation());
    assertEquals(CLAIMED, newDelivery.transportStatus());

    assertFalse(newDelivery.onTheGroundAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    assertTrue(delivery.lastUpdatedOn().before(newDelivery.lastUpdatedOn()));
  }

  public void testUpdateOnHandlingWhenMisdirected() {
    // Unload in Hamburg, which is the wrong location
    HandlingActivity handlingActivity = new HandlingActivity(UNLOAD, HAMBURG, DALLAS_TO_HELSINKI);
    Delivery newDelivery = delivery.onHandling(handlingActivity);

    assertEquals(Voyage.NONE, newDelivery.currentVoyage());
    assertEquals(HAMBURG, newDelivery.lastKnownLocation());
    assertEquals(IN_PORT, newDelivery.transportStatus());

    // Next handling activity is undefined. Need a new itinerary to know what to do.

    assertFalse(newDelivery.onTheGroundAtDestination(routeSpecification));

    assertEquals(ROUTED, newDelivery.routingStatus(itinerary, routeSpecification));

    assertTrue(delivery.lastUpdatedOn().before(newDelivery.lastUpdatedOn()));

    // New route specification, old itinerary
    RouteSpecification newRouteSpecification = routeSpecification.withOrigin(HAMBURG);
    assertEquals(MISROUTED, newDelivery.routingStatus(itinerary, newRouteSpecification));

    Itinerary newItinerary = new Itinerary(
      Leg.deriveLeg(DALLAS_TO_HELSINKI, HAMBURG, STOCKHOLM)
    );

    assertEquals(ROUTED, newDelivery.routingStatus(newItinerary, newRouteSpecification));
    assertEquals(IN_PORT, newDelivery.transportStatus());
  }

  public void testEmptyCtor() {
    new Delivery();
  }
}
