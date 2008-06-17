package se.citerus.dddsample.domain.service;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.Itinerary;
import se.citerus.dddsample.domain.model.Location;
import se.citerus.dddsample.domain.model.RouteSpecification;
import se.citerus.dddsample.domain.repository.CarrierMovementRepository;
import se.citerus.dddsample.domain.repository.LocationRepository;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.dddsample.application.service.api.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.application.service.assembler.ItineraryCandidateDTOAssembler;
import se.citerus.dddsample.application.service.GraphTraversalService;

import java.util.ArrayList;
import java.util.List;

/**
 * Our end of the routing service.
 *
 */
public class RoutingServiceImpl implements RoutingService {

  private GraphTraversalService graphTraversalService;
  private LocationRepository locationRepository;
  private CarrierMovementRepository carrierMovementRepository;

  @Transactional(readOnly = true)
  public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
    final Location origin = routeSpecification.origin();
    final Location destination = routeSpecification.destination();

    final List<ItineraryCandidateDTO> candidateDTOs = graphTraversalService.performHeavyCalculations(
      origin.unLocode().idString(),
      destination.unLocode().idString()
    );
    
    final List<Itinerary> itineraries = new ArrayList<Itinerary>(candidateDTOs.size());

    for (ItineraryCandidateDTO candidateDTO : candidateDTOs) {
      final Itinerary itinerary = new ItineraryCandidateDTOAssembler().fromDTO(
        candidateDTO, carrierMovementRepository, locationRepository
      );
      itineraries.add(itinerary);
    }

    return itineraries;
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
