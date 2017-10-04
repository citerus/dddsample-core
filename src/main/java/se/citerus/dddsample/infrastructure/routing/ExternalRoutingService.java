package se.citerus.dddsample.infrastructure.routing;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.api.TransitEdge;
import com.pathfinder.api.TransitPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.domain.service.RoutingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
  private static final Log log = LogFactory.getLog(ExternalRoutingService.class);

  public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
    /*
      The RouteSpecification is picked apart and adapted to the external API.
     */
    final Location origin = routeSpecification.origin();
    final Location destination = routeSpecification.destination();

    final Properties limitations = new Properties();
    limitations.setProperty("DEADLINE", routeSpecification.arrivalDeadline().toString());

    final List<TransitPath> transitPaths;
      transitPaths = graphTraversalService.findShortestPath(
      origin.unLocode().idString(),
      destination.unLocode().idString(),
      limitations
    );

    /*
     The returned result is then translated back into our domain model.
    */
    final List<Itinerary> itineraries = new ArrayList<Itinerary>();

    for (TransitPath transitPath : transitPaths) {
      final Itinerary itinerary = toItinerary(transitPath);
      // Use the specification to safe-guard against invalid itineraries
      if (routeSpecification.isSatisfiedBy(itinerary)) {
        itineraries.add(itinerary);
      } else {
        log.warn("Received itinerary that did not satisfy the route specification");
      }
    }

    return itineraries;
  }

  private Itinerary toItinerary(TransitPath transitPath) {
    List<Leg> legs = new ArrayList<Leg>(transitPath.getTransitEdges().size());
    for (TransitEdge edge : transitPath.getTransitEdges()) {
      legs.add(toLeg(edge));
    }
    return new Itinerary(legs);
  }

  private Leg toLeg(TransitEdge edge) {
    return new Leg(
      voyageRepository.find(new VoyageNumber(edge.getEdge())),
      locationRepository.find(new UnLocode(edge.getFromNode())),
      locationRepository.find(new UnLocode(edge.getToNode())),
      edge.getFromDate(), edge.getToDate()
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
