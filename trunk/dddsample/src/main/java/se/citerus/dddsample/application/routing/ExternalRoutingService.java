package se.citerus.dddsample.application.routing;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.routingteam.GraphTraversalService;
import se.citerus.routingteam.TransitEdge;
import se.citerus.routingteam.TransitPath;

import java.util.ArrayList;
import java.util.List;

/**
 * Our end of the routing service. This is basically a data model
 * translation layer between our domain model and the API put forward
 * by the routing team, which operates in a different context from us.
 *
 */
public class ExternalRoutingService implements RoutingService {

  private GraphTraversalService graphTraversalService;
  private LocationRepository locationRepository;
  private CarrierMovementRepository carrierMovementRepository;

  @Transactional(readOnly = true)
  public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
    final Location origin = routeSpecification.origin();
    final Location destination = routeSpecification.destination();

    final List<TransitPath> transitPaths = graphTraversalService.performHeavyCalculations(
      origin.unLocode().idString(),
      destination.unLocode().idString()
    );
    
    final List<Itinerary> itineraries = new ArrayList<Itinerary>(transitPaths.size());

    for (TransitPath transitPath : transitPaths) {
      final Itinerary itinerary = toItinerary(transitPath);
      itineraries.add(itinerary);
    }

    return itineraries;
  }

  private Itinerary toItinerary(TransitPath transitPath) {
    List<Leg> legs = new ArrayList(transitPath.getTransitEdges().size());
    for (TransitEdge edge : transitPath.getTransitEdges()) {
      legs.add(toLeg(edge));
    }
    return new Itinerary(legs);
  }

  private Leg toLeg(TransitEdge edge) {
    return new Leg(
      carrierMovementRepository.find(new CarrierMovementId(edge.getCarrierMovementId())),
      locationRepository.find(new UnLocode(edge.getFromUnLocode())),
      locationRepository.find(new UnLocode(edge.getToUnLocode()))
    );
  }

  public void setGraphTraversalService(GraphTraversalService graphTraversalService) {
    this.graphTraversalService = graphTraversalService;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }
}
