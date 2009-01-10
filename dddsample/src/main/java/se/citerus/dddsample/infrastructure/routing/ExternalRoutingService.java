package se.citerus.dddsample.infrastructure.routing;

import com.partner.pathfinder.api.GraphTraversalService;
import com.partner.pathfinder.api.TransitEdge;
import com.partner.pathfinder.api.TransitPath;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;

import java.util.ArrayList;
import java.util.Date;
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
  private VoyageRepository voyageRepository;

  public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
    final Location origin = routeSpecification.origin();
    final Location destination = routeSpecification.destination();

    // TODO send arrival deadline too

    final List<TransitPath> transitPaths = graphTraversalService.findShortestPath(
      origin.unLocode().idString(),
      destination.unLocode().idString()
    );
    
    final List<Itinerary> itineraries = new ArrayList<Itinerary>();

    for (TransitPath transitPath : transitPaths) {
      final Itinerary itinerary = toItinerary(transitPath);
      // Use the specification to safe-guard against invalid itineraries
      if (routeSpecification.isSatisfiedBy(itinerary)) {
        itineraries.add(itinerary);
      } else {
        // TODO log warning/error? Fail?
      }
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
      voyageRepository.find(new VoyageNumber(edge.getCarrierMovementId())),
      locationRepository.find(new UnLocode(edge.getFromUnLocode())),
      locationRepository.find(new UnLocode(edge.getToUnLocode())),
      new Date(), new Date()  // TODO better dates
    );
  }

  public void setGraphTraversalService(GraphTraversalService graphTraversalService) {
    this.graphTraversalService = graphTraversalService;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setVoyageRepository(VoyageRepository voyageRepository) {
    this.voyageRepository = voyageRepository;
  }
}
