package se.citerus.dddsample.service;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.LocationRepository;

import java.util.ArrayList;
import java.util.List;

public final class BookingServiceImpl implements BookingService {

  private CargoRepository cargoRepository;
  private LocationRepository locationRepository;

  private final Log logger = LogFactory.getLog(getClass());

  @Transactional(readOnly = false)
  public TrackingId registerNewCargo(final UnLocode originUnLocode, final UnLocode destinationUnLocode) {
    Validate.notNull(originUnLocode);
    Validate.notNull(destinationUnLocode);

    final TrackingId trackingId = cargoRepository.nextTrackingId();
    final Location origin = locationRepository.find(originUnLocode);
    final Location destination = locationRepository.find(destinationUnLocode);
    Cargo cargo = new Cargo(trackingId, origin, destination);

    cargoRepository.save(cargo);
    logger.info("Registered new cargo with tracking id " + cargo.trackingId().idString());

    return cargo.trackingId();
  }

  @Transactional(readOnly = true)
  public List<UnLocode> listShippingLocations() {
    final List<Location> allLocations = locationRepository.findAll();
    final List<UnLocode> unlocodes = new ArrayList<UnLocode>(allLocations.size());
    for (Location location : allLocations) {
      unlocodes.add(location.unLocode());
    }
    return unlocodes;
  }

  @Transactional(readOnly = true)
  public List<Cargo> listAllCargos() {
    final List<Cargo> allCargos = cargoRepository.findAll();
    // TODO: specification pattern might be useful here, too
    return allCargos;
  }

  @Transactional(readOnly = true)
  public Cargo loadCargoForRouting(final TrackingId trackingId) {
    Validate.notNull(trackingId);

    // TODO obtain offline lock
    final Cargo cargo = cargoRepository.find(trackingId);

    return cargo;
  }

  @Transactional(readOnly = false)
  public void assignCargoToRoute(final TrackingId trackingId, final Itinerary newItinerary) {
    Validate.notNull(trackingId);
    Validate.notNull(newItinerary);

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      throw new IllegalArgumentException("Can't assign itinerary to non-existing cargo " + trackingId);
    }

    // Assign the new itinerary to the cargo
    cargo.attachItinerary(newItinerary);
    cargoRepository.save(cargo);

    logger.info("Assigned cargo " + trackingId + " to new route");

    // TODO release offline lock
  }

  public void setCargoRepository(final CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setLocationRepository(final LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

}
