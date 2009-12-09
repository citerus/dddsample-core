package se.citerus.dddsample.tracking.core.domain.scenario;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import org.junit.Test;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage.NONE;
import se.citerus.dddsample.tracking.core.domain.service.RoutingService;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import java.util.List;

public class CargoLifecycle {

  @Test
  public void cargoIsMisdirectedAndRerouted() throws Exception {
    
    TrackingId trackingId = new TrackingId("ABC");
    RouteSpecification routeSpecification =
      new RouteSpecification(HONGKONG, STOCKHOLM, toDate("2009-03-18"));
    Cargo cargo = new Cargo(trackingId, routeSpecification);

    // Initial state, before routing
    assertThat(cargo.transportStatus(), is(NOT_RECEIVED));
    assertThat(cargo.routingStatus(), is(NOT_ROUTED));
    assertFalse(cargo.isMisdirected());
    assertNull(cargo.estimatedTimeOfArrival());
    assertNull(cargo.nextExpectedActivity());



    // Route: Hongkong - Long Beach - New York - Stockholm
    List<Itinerary> itineraries =
      routingService.fetchRoutesForSpecification(cargo.routeSpecification());
    Itinerary itinerary = selectAppropriateRoute(itineraries);
    cargo.assignToRoute(itinerary);

    // Routed
    assertThat(cargo.transportStatus(), is(NOT_RECEIVED));
    assertThat(cargo.routingStatus(), is(ROUTED));
    assertThat(cargo.nextExpectedActivity(), is(receiveIn(HONGKONG)));
    assertNotNull(cargo.estimatedTimeOfArrival());











    // Received
    cargo.handled(receiveIn(HONGKONG));

    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertThat(cargo.lastKnownLocation(), is(HONGKONG));

















    // Loaded
    cargo.handled(loadOnto(pacific1).in(HONGKONG));

    assertThat(cargo.currentVoyage(), is(pacific1));
    assertThat(cargo.lastKnownLocation(), is(HONGKONG));
    assertThat(cargo.transportStatus(), is(ONBOARD_CARRIER));
    assertThat(cargo.nextExpectedActivity(), is(unloadOff(pacific1).in(LONGBEACH)));
    assertFalse(cargo.isMisdirected());














    // Unloaded
    cargo.handled(unloadOff(pacific1).in(LONGBEACH));

    assertThat(cargo.currentVoyage(), is(NONE));
    assertThat(cargo.lastKnownLocation(), is(LONGBEACH));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertFalse(cargo.isMisdirected());
    assertThat(cargo.nextExpectedActivity(), is(loadOnto(continental1).in(LONGBEACH)));















    // Unloaded in Rotterdam, wasn't supposed to happen
    cargo.handled(unloadOff(pacific2).in(ROTTERDAM));

    // Misdirected
    assertTrue(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(ROTTERDAM));
    assertThat(cargo.transportStatus(), is(IN_PORT));













    // Reroute: specify new route
    RouteSpecification currentRouteSpec = cargo.routeSpecification();
    RouteSpecification newRouteSpec =
        currentRouteSpec.withOrigin(cargo.lastKnownLocation());
    cargo.specifyNewRoute(newRouteSpec);

    assertThat(cargo.routingStatus(), is(MISROUTED));










    // Assign to new route
    List<Itinerary> available = routingService.fetchRoutesForSpecification(newRouteSpec);
    Itinerary newItinerary = selectAppropriateRoute(available);
    cargo.assignToRoute(newItinerary);

    assertThat(cargo.routingStatus(), is(ROUTED));
    assertThat(cargo.nextExpectedActivity(), is(loadOnto(v300).in(ROTTERDAM)));












    // Loaded, back on track
    cargo.handled(loadOnto(v300).in(ROTTERDAM));
    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(ROTTERDAM));
    assertThat(cargo.transportStatus(), is(ONBOARD_CARRIER));














  }

  private Itinerary selectAppropriateRoute(List<Itinerary> itineraries) {
    return itineraries.get(0);
  }

  RoutingService routingService = new ScenarioStubRoutingService();

  private static class ScenarioStubRoutingService implements RoutingService {

    private static final Itinerary itinerary1 = new Itinerary(asList(
      new Leg(pacific1, HONGKONG, LONGBEACH, toDate("2009-03-03"), toDate("2009-03-09")),
      new Leg(continental1, LONGBEACH, NEWYORK, toDate("2009-03-10"), toDate("2009-03-14")),
      new Leg(pacific2, NEWYORK, STOCKHOLM, toDate("2009-03-07"), toDate("2009-03-11"))
    ));

    private static final Itinerary itinerary2 = new Itinerary(asList(
      new Leg(v300, ROTTERDAM, HAMBURG, toDate("2009-03-10"), toDate("2009-03-12")),
      new Leg(v400, HAMBURG, STOCKHOLM, toDate("2009-03-14"), toDate("2009-03-15"))
    ));

    public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
      if (routeSpecification.origin().sameAs(HONGKONG)) {
        // Hongkong - Long Beach - New York - Stockholm, initial routing
        return asList(itinerary1);
      } else if (routeSpecification.origin().sameAs(ROTTERDAM)) {
        // Rotterdam - Hamburg - Stockholm, rerouting misdirected cargo from Rotterdam
        return asList(itinerary2);
      } else {
        return emptyList();
      }
    }

  }

}