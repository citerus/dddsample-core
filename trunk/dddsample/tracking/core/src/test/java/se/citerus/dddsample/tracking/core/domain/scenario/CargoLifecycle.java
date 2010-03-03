package se.citerus.dddsample.tracking.core.domain.scenario;

import org.junit.Test;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import se.citerus.dddsample.tracking.core.domain.service.RoutingService;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.TrackingIdFactoryInMem;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage.NONE;

public class CargoLifecycle {

  @Test
  public void cargoIsProperlyDelivered() throws Exception {
    Cargo cargo = setupCargoFromHongkongToStockholm();

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
    assertThat(cargo.customsClearancePoint(), is(STOCKHOLM));

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

    assertFalse(cargo.isMisdirected());
    assertThat(cargo.currentVoyage(), is(NONE));
    assertThat(cargo.lastKnownLocation(), is(LONGBEACH));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertThat(cargo.nextExpectedActivity(), is(loadOnto(continental1).in(LONGBEACH)));

    cargo.handled(loadOnto(continental1).in(LONGBEACH));

    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(LONGBEACH));
    assertThat(cargo.transportStatus(), is(ONBOARD_CARRIER));
    assertThat(cargo.currentVoyage(), is(continental1));
    assertThat(cargo.nextExpectedActivity(), is(unloadOff(continental1).in(NEWYORK)));

    cargo.handled(unloadOff(continental1).in(NEWYORK));

    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(NEWYORK));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertThat(cargo.currentVoyage(), is(NONE));
    assertThat(cargo.nextExpectedActivity(), is(loadOnto(atlantic2).in(NEWYORK)));

    cargo.handled(loadOnto(atlantic2).in(NEWYORK));

    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(NEWYORK));
    assertThat(cargo.transportStatus(), is(ONBOARD_CARRIER));
    assertThat(cargo.currentVoyage(), is(atlantic2));
    assertThat(cargo.nextExpectedActivity(), is(unloadOff(atlantic2).in(STOCKHOLM)));

    cargo.handled(unloadOff(atlantic2).in(STOCKHOLM));

    assertFalse(cargo.isReadyToClaim());
    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(STOCKHOLM));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertThat(cargo.currentVoyage(), is(NONE));
    assertThat(cargo.nextExpectedActivity(), is(customsIn(STOCKHOLM)));

    cargo.handled(customsIn(STOCKHOLM));

    assertTrue(cargo.isReadyToClaim());
    assertThat(cargo.nextExpectedActivity(), is(claimIn(STOCKHOLM)));

    cargo.handled(claimIn(STOCKHOLM));
    
    assertNull(cargo.nextExpectedActivity());
  }

  // TODO misdirected cargo, loaded onto wrong voyage
                                                      
  @Test
  public void cargoIsMisdirectedAndRerouted() throws Exception {

    Cargo cargo = setupCargoFromHongkongToStockholm();

    // Initial state, before routing
    assertThat(cargo.transportStatus(), is(NOT_RECEIVED));
    assertThat(cargo.routingStatus(), is(NOT_ROUTED));
    assertFalse(cargo.isMisdirected());
    assertNull(cargo.estimatedTimeOfArrival());
    assertNull(cargo.nextExpectedActivity());

    // Route: Hongkong - Long Beach - New York - Stockholm
    List<Itinerary> itineraries = routingService.fetchRoutesForSpecification(cargo.routeSpecification());
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

    // Unloaded in Seattle, wasn't supposed to happen
    cargo.handled(unloadOff(pacific1).in(SEATTLE));

    // Misdirected
    assertTrue(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(SEATTLE));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertNull(cargo.nextExpectedActivity());
    assertNull(cargo.estimatedTimeOfArrival());

    // Reroute: specify new route

    // Assign to new route
    List<Itinerary> available = routingService.fetchRoutesForSpecification(
      cargo.routeSpecification().withOrigin(cargo.earliestReroutingLocation())
    );

    Itinerary newItinerary = selectAppropriateRoute(available);
    Itinerary mergedItinerary = cargo.itineraryMergedWith(newItinerary);
    cargo.assignToRoute(mergedItinerary);

    assertFalse(cargo.isMisdirected());
    assertThat(cargo.routingStatus(), is(ROUTED));
    assertThat(cargo.nextExpectedActivity(), is(loadOnto(continental3).in(SEATTLE)));

    // Loaded, back on track
    cargo.handled(loadOnto(continental3).in(SEATTLE));
    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(SEATTLE));
    assertThat(cargo.transportStatus(), is(ONBOARD_CARRIER));

    // Etc
  }

  @Test
  public void cargoIsLoadedOntoWrongVoyage() throws Exception {

    Cargo cargo = setupCargoFromHongkongToStockholm();

    // Initial state, before routing
    assertThat(cargo.transportStatus(), is(NOT_RECEIVED));
    assertThat(cargo.routingStatus(), is(NOT_ROUTED));
    assertFalse(cargo.isMisdirected());
    assertNull(cargo.estimatedTimeOfArrival());
    assertNull(cargo.nextExpectedActivity());

    // Route: Hongkong - Long Beach - New York - Stockholm
    List<Itinerary> itineraries = routingService.fetchRoutesForSpecification(cargo.routeSpecification());
    Itinerary itinerary = selectAppropriateRoute(itineraries);
    cargo.assignToRoute(itinerary);

    // Routed
    assertThat(cargo.transportStatus(), is(NOT_RECEIVED));
    assertThat(cargo.routingStatus(), is(ROUTED));
    assertThat(cargo.nextExpectedActivity(), is(receiveIn(HONGKONG)));
    assertThat(cargo.estimatedTimeOfArrival(), is(toDate("2009-03-26")));

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

    // Unload
    cargo.handled(unloadOff(pacific1).in(LONGBEACH));
    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(LONGBEACH));
    assertThat(cargo.transportStatus(), is(IN_PORT));
    assertThat(cargo.nextExpectedActivity(), is(loadOnto(continental1).in(LONGBEACH)));

    // Load onto wrong voyage
    cargo.handled(loadOnto(pacific2).in(LONGBEACH));
    assertTrue(cargo.isMisdirected());
    assertThat(cargo.transportStatus(), is(ONBOARD_CARRIER));
    assertNull(cargo.nextExpectedActivity());
    assertNull(cargo.estimatedTimeOfArrival());

    // Reroute: specify new route

    assertThat(cargo.earliestReroutingLocation(), is(SEATTLE));

    // Assign to new route
    List<Itinerary> available = routingService.fetchRoutesForSpecification(
      cargo.routeSpecification().withOrigin(cargo.earliestReroutingLocation())
    );

    Itinerary newItinerary = selectAppropriateRoute(available);

    Itinerary mergedItinerary = cargo.itineraryMergedWith(newItinerary);
    cargo.assignToRoute(mergedItinerary);

    // No longer misdirected
    assertFalse(cargo.isMisdirected());
    assertThat(cargo.routingStatus(), is(ROUTED));
    assertThat(cargo.nextExpectedActivity(), is(unloadOff(pacific2).in(SEATTLE)));

    // Loaded
    cargo.handled(unloadOff(pacific2).in(SEATTLE));

    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(SEATTLE));
    assertThat(cargo.transportStatus(), is(IN_PORT));

    // Etc
  }


  @Test
  public void customerRequestsChangeOfDestination() throws Exception {
    Cargo cargo = setupCargoFromHongkongToStockholm();

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

    // Customer wants cargo to go to Rotterdam instead of Stockholm
    RouteSpecification toRotterdam = cargo.routeSpecification().
      withDestination(ROTTERDAM);

    cargo.specifyNewRoute(toRotterdam);

    // Misrouted
    assertThat(cargo.routingStatus(), is(MISROUTED));
    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(LONGBEACH));
    assertThat(cargo.transportStatus(), is(IN_PORT));

    // Assign to new route
    List<Itinerary> available = routingService.fetchRoutesForSpecification(cargo.routeSpecification());
    Itinerary newItinerary = selectAppropriateRoute(available);
    Itinerary mergedItinerary = cargo.itineraryMergedWith(newItinerary);

    cargo.assignToRoute(mergedItinerary);

    assertThat(cargo.routingStatus(), is(ROUTED));
    assertThat(cargo.nextExpectedActivity(), is(loadOnto(continental2).in(LONGBEACH)));

    // Loaded, back on track
    cargo.handled(loadOnto(continental2).in(LONGBEACH));
    assertFalse(cargo.isMisdirected());
    assertThat(cargo.lastKnownLocation(), is(LONGBEACH));
    assertThat(cargo.transportStatus(), is(ONBOARD_CARRIER));
    assertThat(cargo.nextExpectedActivity(), is(unloadOff(continental2).in(NEWYORK)));

    // Fast forward a bit
    cargo.handled(unloadOff(continental2).in(NEWYORK));
    cargo.handled(loadOnto(atlantic1).in(NEWYORK));

    // Cargo enters its destination customs zone
    cargo.handled(unloadOff(atlantic1).in(ROTTERDAM));
    assertThat(cargo.nextExpectedActivity(), is(customsIn(ROTTERDAM)));
    cargo.handled(customsIn(ROTTERDAM));
    assertThat(cargo.nextExpectedActivity(), is(claimIn(ROTTERDAM)));
  }

  private Cargo setupCargoFromHongkongToStockholm() {
    TrackingId trackingId = trackingIdFactory.nextTrackingId();
    Date arrivalDeadline = toDate("2009-04-10");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, STOCKHOLM, arrivalDeadline);

    return new Cargo(trackingId, routeSpecification);
  }

  // Stubbed out customer selection process
  private Itinerary selectAppropriateRoute(List<Itinerary> itineraries) {
    return itineraries.get(0);
  }

  private final RoutingService routingService = new ScenarioStubRoutingService();

  private final TrackingIdFactory trackingIdFactory = new TrackingIdFactoryInMem();

  private static class ScenarioStubRoutingService implements RoutingService {

    private static final Itinerary itinerary1 = new Itinerary(
      Leg.deriveLeg(pacific1, HONGKONG, LONGBEACH),
      Leg.deriveLeg(continental1, LONGBEACH, NEWYORK),
      Leg.deriveLeg(atlantic2, NEWYORK, STOCKHOLM)
    );

    private static final Itinerary itinerary2 = new Itinerary(
      Leg.deriveLeg(continental3, SEATTLE, NEWYORK),
      Leg.deriveLeg(atlantic2, NEWYORK, STOCKHOLM)
    );

    private static final Itinerary itinerary3 = new Itinerary(
      Leg.deriveLeg(continental2, LONGBEACH, NEWYORK),
      Leg.deriveLeg(atlantic1, NEWYORK, ROTTERDAM)
    );

    public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
      if (routeSpecification.origin().sameAs(HONGKONG) && routeSpecification.destination().sameAs(STOCKHOLM)) {
        // Hongkong - Long Beach - New York - Stockholm, initial routing
        return asList(itinerary1);
      } else if (routeSpecification.origin().sameAs(SEATTLE) && routeSpecification.destination().sameAs(STOCKHOLM)) {
        // Rotterdam - Hamburg - Stockholm, rerouting misdirected cargo from Rotterdam
        return asList(itinerary2);
      } else if (routeSpecification.origin().sameAs(HONGKONG) && routeSpecification.destination().sameAs(ROTTERDAM)) {
        // Customer requested change of destination
        return asList(itinerary3);
      } else {
        throw new IllegalStateException("No stubbed data for " + routeSpecification);
      }
    }

  }

}