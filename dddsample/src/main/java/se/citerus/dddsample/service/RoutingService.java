package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Itinerary;
import se.citerus.dddsample.domain.Specification;

import java.util.Set;

/**
 *  
 */
public interface RoutingService {

  Set<Itinerary> calculatePossibleRoutes(Cargo cargo, Specification specification);

}
