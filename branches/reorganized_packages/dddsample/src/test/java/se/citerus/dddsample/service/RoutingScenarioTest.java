package se.citerus.dddsample.service;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;

import java.util.Date;
import java.util.List;

public class RoutingScenarioTest extends TestCase {

  RoutingService routingService;
  CargoRepository cargoRepository;
  HandlingEventService handlingEventService;

  // TODO work on this
  public void xtestCalculateRoute() throws Exception {

    TrackingId trackingId = new TrackingId("XYZ123");
    Cargo cargo = cargoRepository.find(trackingId);

    Date twoWeeksFromNow = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14);

    RouteSpecification routeSpecification = RouteSpecification.forCargo(cargo, twoWeeksFromNow);

    /*
      The routing service calculates a number of possible routes that
      satisfy the given specification (must arrive in three days, must not
      cost more than $10,000 etc).
     */
    List<Itinerary> itineraryCandidates = routingService.fetchRoutesForSpecification(routeSpecification);

    /*
      Someone, or something, selects the most appropriate itinerary and
      assigns that itinerary to the cargo.
     */
    Itinerary itinerary = stubbedItinerarySelection(itineraryCandidates);
    cargo.attachItinerary(itinerary);

    /*
      A number of events occur, all of which are according to plan
     */
    handlingEventService.register(new Date(), trackingId, new CarrierMovementId("A001"), new UnLocode("SESTO"), null);
    handlingEventService.register(new Date(), trackingId, new CarrierMovementId("B002"), null, null);
    handlingEventService.register(new Date(), trackingId, new CarrierMovementId("C003"), null, null);

    /*
      Cargo should not be misdirected at this point.
     */
    assertFalse(cargo.isMisdirected());

    /*
      An unexpected event occur.
     */
    handlingEventService.register(null, null, null, null, null);

    /*
      Now the cargo is misdirected.
     */
    assertTrue(cargo.isMisdirected());
  }

  public void testRun() throws Exception
  {
    
  }


  private Itinerary stubbedItinerarySelection(List<Itinerary> itineraryCandidates) {
    return itineraryCandidates.get(0);
  }

}
