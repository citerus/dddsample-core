package se.citerus.dddsample.domain.service;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.*;
import se.citerus.dddsample.domain.repository.CargoRepository;
import se.citerus.dddsample.domain.repository.LocationRepository;
import se.citerus.dddsample.domain.service.BookingService;
import se.citerus.dddsample.domain.service.RoutingService;

import java.util.Date;
import java.util.List;

public final class BookingServiceImpl implements BookingService {

  private CargoRepository cargoRepository;
  private LocationRepository locationRepository;
  private RoutingService routingService;

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
  public Cargo loadCargoForRouting(final TrackingId trackingId) {
    Validate.notNull(trackingId);

    // TODO obtain offline lock
    final Cargo cargo = cargoRepository.find(trackingId);

    return cargo;
  }

  @Transactional(readOnly = true)
  public List<Itinerary> requestPossibleRoutesForCargo(TrackingId trackingId) {
    final Cargo cargo = loadCargoForRouting(trackingId);
    final RouteSpecification routeSpecification = RouteSpecification.forCargo(cargo, new Date());

    return routingService.fetchRoutesForSpecification(routeSpecification);
  }

  @Transactional(readOnly = false)
  public void assignCargoToRoute(final TrackingId trackingId, final Itinerary newItinerary) {
    Validate.notNull(trackingId);
    Validate.notNull(newItinerary);

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      throw new IllegalArgumentException("Can't assign itinerary to non-existing cargo " + trackingId);
    }

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

  public void setRoutingService(RoutingService routingService) {
    this.routingService = routingService;
  }
}
