package se.citerus.dddsample.tracking.core.infrastructure.routing;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.api.TransitEdge;
import com.pathfinder.api.TransitPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Itinerary;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Leg;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.tracking.core.domain.service.RoutingService;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Our end of the routing service. This is basically a data model
 * translation layer between our domain model and the API put forward
 * by the routing team, which operates in a different context from us.
 */
@Service
public class ExternalRoutingService implements RoutingService {

  private final GraphTraversalService graphTraversalService;
  private final LocationRepository locationRepository;
  private final VoyageRepository voyageRepository;
  private static final Log log = LogFactory.getLog(ExternalRoutingService.class);

  @Autowired
  public ExternalRoutingService(final GraphTraversalService graphTraversalService,
                                final LocationRepository locationRepository,
                                final VoyageRepository voyageRepository) {
    this.graphTraversalService = graphTraversalService;
    this.locationRepository = locationRepository;
    this.voyageRepository = voyageRepository;
  }

  public List<Itinerary> fetchRoutesForSpecification(final RouteSpecification routeSpecification) {
    /*
      The RouteSpecification is picked apart and adapted to the external API.
     */
    final Location origin = routeSpecification.origin();
    final Location destination = routeSpecification.destination();

    final Properties limitations = new Properties();
    limitations.setProperty("DEADLINE", routeSpecification.arrivalDeadline().toString());

    final List<TransitPath> transitPaths;
    try {
      transitPaths = graphTraversalService.findShortestPath(
        origin.unLocode().stringValue(),
        destination.unLocode().stringValue(),
        limitations
      );
    } catch (RemoteException e) {
      log.error(e, e);
      return Collections.emptyList();
    }

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

  private Itinerary toItinerary(final TransitPath transitPath) {
    final List<Leg> legs = new ArrayList<Leg>(transitPath.getTransitEdges().size());
    for (TransitEdge edge : transitPath.getTransitEdges()) {
      legs.add(toLeg(edge));
    }
    return new Itinerary(legs);
  }

  private Leg toLeg(final TransitEdge edge) {
    return new Leg(
      voyageRepository.find(new VoyageNumber(edge.getVoyageNumber())),
      locationRepository.find(new UnLocode(edge.getFromUnLocode())),
      locationRepository.find(new UnLocode(edge.getToUnLocode())),
      edge.getFromDate(), edge.getToDate()
    );
  }

}
