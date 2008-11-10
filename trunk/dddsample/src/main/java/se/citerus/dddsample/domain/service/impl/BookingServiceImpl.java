package se.citerus.dddsample.domain.service.impl;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.BookingService;
import se.citerus.dddsample.domain.service.RoutingService;

import java.util.Date;
import java.util.List;

public final class BookingServiceImpl implements BookingService {

  private final CargoRepository cargoRepository;
  private final LocationRepository locationRepository;
  private final RoutingService routingService;
  private final Log logger = LogFactory.getLog(getClass());

  public BookingServiceImpl(CargoRepository cargoRepository, LocationRepository locationRepository, RoutingService routingService) {
    this.cargoRepository = cargoRepository;
    this.locationRepository = locationRepository;
    this.routingService = routingService;
  }

  public TrackingId bookNewCargo(final UnLocode originUnLocode, final UnLocode destinationUnLocode) {
    Validate.notNull(originUnLocode);
    Validate.notNull(destinationUnLocode);

    // TODO cargo factory? tracking id factory?
    final TrackingId trackingId = cargoRepository.nextTrackingId();
    final Location origin = locationRepository.find(originUnLocode);
    final Location destination = locationRepository.find(destinationUnLocode);
    Cargo cargo = new Cargo(trackingId, origin, destination);

    cargoRepository.save(cargo);
    logger.info("Registered new cargo with tracking id " + cargo.trackingId().idString());

    return cargo.trackingId();
  }

  public List<Itinerary> requestPossibleRoutesForCargo(TrackingId trackingId) {
    Validate.notNull(trackingId);
    
    final Cargo cargo = cargoRepository.find(trackingId);
    final RouteSpecification routeSpecification = new RouteSpecification(cargo.origin(), cargo.destination(), new Date());

    return routingService.fetchRoutesForSpecification(routeSpecification);
  }

}
