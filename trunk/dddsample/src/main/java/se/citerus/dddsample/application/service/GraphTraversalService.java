package se.citerus.dddsample.application.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.application.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.application.service.dto.assembler.ItineraryCandidateDTOAssembler;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GraphTraversalService {

  private LocationRepository locationRepository;
  private CarrierMovementRepository carrierMovementRepository;

  Random random = new Random();

  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public List<ItineraryCandidateDTO> performHeavyCalculations(String originUnLocode, String destinationUnLocode) {
    List<Location> allLocations = locationRepository.findAll();
    Location origin = locationRepository.find(new UnLocode(originUnLocode));
    Location destination = locationRepository.find(new UnLocode(destinationUnLocode));
    
    allLocations.remove(origin);
    allLocations.remove(destination);

    final int candidateCount = getRandomNumberOfCandidates();
    final List<ItineraryCandidateDTO> candidates = new ArrayList<ItineraryCandidateDTO>(candidateCount);
    final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

    for (int i = 0; i < candidateCount; i++) {
      allLocations = getRandomChunkOfLocations(allLocations);
      final List<Leg> legs = new ArrayList<Leg>(allLocations.size() - 1);
      final Location firstLegTo = allLocations.get(0);

      final CarrierMovement cm1 = carrierMovementRepository.find(new CarrierMovementId("CAR_002"));
      legs.add(new Leg(cm1, origin, firstLegTo));

      for (int j = 0; j < allLocations.size() - 1; j++) {
        legs.add(new Leg(getRandomCarrierMovement(), allLocations.get(j), allLocations.get(j + 1)));
      }

      final Location lastLegFrom = allLocations.get(allLocations.size() - 1);
      legs.add(new Leg(getRandomCarrierMovement(), lastLegFrom, destination));

      final Itinerary itinerary = new Itinerary(legs);
      candidates.add(assembler.toDTO(itinerary));
    }

    return candidates;
  }

  private CarrierMovement getRandomCarrierMovement() {
    // TODO create new CMs on the fly, with better names than "car 1,2,3"
    final CarrierMovementId id = new CarrierMovementId("CAR_00" + (random.nextInt(9) + 1));
    return carrierMovementRepository.find(id);
  }

  private int getRandomNumberOfCandidates() {
    return 1 + random.nextInt(4);
  }

  private List<Location> getRandomChunkOfLocations(List<Location> allLocations) {
    Collections.shuffle(allLocations);
    final int total = allLocations.size();
    final int chunk = total > 4 ? (total - 4) + random.nextInt(5) : total;
    return allLocations.subList(0, chunk);
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }
}
