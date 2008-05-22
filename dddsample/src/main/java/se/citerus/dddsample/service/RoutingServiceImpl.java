package se.citerus.dddsample.service;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.LocationRepository;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.service.dto.assembler.ItineraryCandidateDTOAssembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Simple routing service implementation that randomly creates a number
 * of different itineraries.
 */
public class RoutingServiceImpl implements RoutingService {

  private LocationRepository locationRepository;
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;

  Random random = new Random();

  @Transactional(readOnly = true)
  public List<ItineraryCandidateDTO> calculatePossibleRoutes(TrackingId trackingId, Specification specification) {
    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      return Collections.emptyList();
    }

    List<Location> allLocations = locationRepository.findAll();

    allLocations.remove(cargo.origin());
    allLocations.remove(cargo.finalDestination());

    final int candidateCount = getRandomNumberOfCandidates();
    final List<ItineraryCandidateDTO> candidates = new ArrayList<ItineraryCandidateDTO>(candidateCount);
    final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

    for (int i = 0; i < candidateCount; i++) {
      allLocations = getRandomChunkOfLocations(allLocations);
      final List<Leg> legs = new ArrayList<Leg>(allLocations.size() - 1);
      final Location firstLegTo = allLocations.get(0);

      final CarrierMovement cm1 = carrierMovementRepository.find(new CarrierMovementId("CAR_002"));
      legs.add(new Leg(cm1, cargo.origin(), firstLegTo));

      for (int j = 0; j < allLocations.size() - 1; j++) {
        legs.add(new Leg(getRandomCarrierMovement(), allLocations.get(j), allLocations.get(j + 1)));
      }

      final Location lastLegFrom = allLocations.get(allLocations.size() - 1);
      legs.add(new Leg(getRandomCarrierMovement(), lastLegFrom, cargo.finalDestination()));

      final Itinerary itinerary = new Itinerary(legs);
      candidates.add(assembler.toDTO(itinerary));
    }

    return candidates;
  }

  private List<Location> getRandomChunkOfLocations(List<Location> allLocations) {
    Collections.shuffle(allLocations);
    final int total = allLocations.size();
    final int chunk = total > 4 ? (total - 4) + random.nextInt(5) : total;
    return allLocations.subList(0, chunk);
  }

  private int getRandomNumberOfCandidates() {
    return 1 + random.nextInt(4);
  }

  private CarrierMovement getRandomCarrierMovement() {
    final CarrierMovementId id = new CarrierMovementId("CAR_00" + (random.nextInt(9) + 1));
    return carrierMovementRepository.find(id);
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }
}
