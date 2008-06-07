package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.RouteSpecification;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;

import java.util.List;

/**
 *
 */
public interface RoutingService {

  List<ItineraryCandidateDTO> calculatePossibleRoutes(TrackingId trackingId, RouteSpecification routeSpecification);

}
