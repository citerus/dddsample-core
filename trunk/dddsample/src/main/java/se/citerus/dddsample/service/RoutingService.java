package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.Itinerary;
import se.citerus.dddsample.domain.RouteSpecification;

import java.util.List;

/**
 * Routing service.
 *
 */
public interface RoutingService {

  /**
   * @param routeSpecification route specification
   * @return A list of itineraries that satisfy the specification. May be an empty list if no route is found.
   */
  List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification);

}
