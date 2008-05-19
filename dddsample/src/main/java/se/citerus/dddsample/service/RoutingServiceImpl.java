package se.citerus.dddsample.service;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.LocationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Simple routing service implementation that randomly creates a number
 * of different itineraries.
 *
 */
public class RoutingServiceImpl implements RoutingService {

  LocationRepository locationRepository;
  CargoRepository cargoRepository;

  Random random = new Random();

  @Transactional(readOnly = true)
  public List<Itinerary> calculatePossibleRoutes(TrackingId trackingId, Specification specification) {
    Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      return Collections.emptyList();
    }
    List<Location> allLocations = locationRepository.findAll();

    allLocations.remove(cargo.origin());
    allLocations.remove(cargo.finalDestination());

    int candidateCount = getRandomNumberOfCandidates();
    List<Itinerary> candidates = new ArrayList<Itinerary>(candidateCount);
    for (int i = 0; i < candidateCount; i++) {
      allLocations = getRandomChunkOfLocations(allLocations);
      List<Leg> legs = new ArrayList<Leg>(allLocations.size() - 1);

      Location firstLegTo = allLocations.get(0);
      legs.add(new Leg(new CarrierMovementId("CAR_002"), cargo.origin(), firstLegTo));

      for (int j = 0; j < allLocations.size() - 1 ; j++) {
        legs.add(new Leg(
          getRandomCarrierMovementId(),
          allLocations.get(j), allLocations.get(j + 1)));
      }

      Location lastLegFrom = allLocations.get(allLocations.size() - 1);
      legs.add(new Leg(getRandomCarrierMovementId(), lastLegFrom, cargo.finalDestination()));

      candidates.add(new Itinerary(legs));
    }

    return candidates;
  }

  private List<Location> getRandomChunkOfLocations(List<Location> allLocations) {
    Collections.shuffle(allLocations);
    int total = allLocations.size();
    int chunk = total > 4 ? (total - 4) + random.nextInt(5) : total;
    return allLocations.subList(0, chunk);
  }

  private int getRandomNumberOfCandidates() {
    return 1 + random.nextInt(4);
  }

  private CarrierMovementId getRandomCarrierMovementId() {
    return new CarrierMovementId("CM" + random.nextInt(1000));
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

}
