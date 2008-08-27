package se.citerus.dddsample.domain.service.impl;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
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
  public TrackingId bookNewCargo(final UnLocode originUnLocode, final UnLocode destinationUnLocode) {
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
  public List<Itinerary> requestPossibleRoutesForCargo(TrackingId trackingId) {
    Validate.notNull(trackingId);
    
    final Cargo cargo = cargoRepository.find(trackingId);
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
