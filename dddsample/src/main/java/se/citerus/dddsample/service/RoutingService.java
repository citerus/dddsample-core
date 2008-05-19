package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.Itinerary;
import se.citerus.dddsample.domain.Specification;
import se.citerus.dddsample.domain.TrackingId;

import java.util.List;

/**
 *  
 */
public interface RoutingService {

  List<Itinerary> calculatePossibleRoutes(TrackingId trackingId, Specification specification);

}
