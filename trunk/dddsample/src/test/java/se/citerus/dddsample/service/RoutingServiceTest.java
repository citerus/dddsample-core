package se.citerus.dddsample.service;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;

import java.util.Date;
import java.util.Set;

public class RoutingServiceTest extends TestCase {

  RoutingService routingService;
  CargoRepository cargoRepository;
  HandlingEventService handlingEventService;

  public void testCalculateRoute() throws Exception {
    TrackingId trackingId = new TrackingId("XYZ123");
    Cargo cargo = cargoRepository.find(trackingId);

    Specification specification = null;

    /*
      The routing service calculates a number of possible routes that
      satisfy the given specification (must arrive in three days, must not
      cost more than $10,000 etc).
     */
    Set<Itinerary> itineraryCandidates = routingService.calculatePossibleRoutes(cargo, specification);

    /*
      Someone, or something, selects the most appropriate itinerary and
      assigns that itinerary to the cargo.
     */
    Itinerary itinerary = selectItinerary(itineraryCandidates);
    cargo.assignItinerary(itinerary);

    /*
      A number of events occur, all of which are according to plan
     */
    handlingEventService.register(new Date(), trackingId, new CarrierMovementId("A001"), new UnLocode("SE","STO"), null);
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


  // Stub for the itinerary selection process
  private Itinerary selectItinerary(Set<Itinerary> itineraryCandidates) {
    return itineraryCandidates.iterator().next();
  }

}
