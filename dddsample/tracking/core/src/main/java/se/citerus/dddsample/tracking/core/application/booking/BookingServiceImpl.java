package se.citerus.dddsample.tracking.core.application.booking;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.service.RoutingService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public final class BookingServiceImpl implements BookingService {

  private final RoutingService routingService;
  private final CargoRepository cargoRepository;
  private final LocationRepository locationRepository;
  private final Log logger = LogFactory.getLog(getClass());
  private final TrackingIdFactory trackingIdFactory;

  @Autowired
  public BookingServiceImpl(final RoutingService routingService,
                            final TrackingIdFactory trackingIdFactory,
                            final CargoRepository cargoRepository,
                            final LocationRepository locationRepository) {
    this.routingService = routingService;
    this.trackingIdFactory = trackingIdFactory;
    this.cargoRepository = cargoRepository;
    this.locationRepository = locationRepository;
  }

  @Override
  @Transactional
  public TrackingId bookNewCargo(final UnLocode originUnLocode,
                                 final UnLocode destinationUnLocode,
                                 final Date arrivalDeadline) {
    final TrackingId trackingId = trackingIdFactory.nextTrackingId();
    final Location origin = locationRepository.find(originUnLocode);
    final Location destination = locationRepository.find(destinationUnLocode);
    final RouteSpecification routeSpecification = new RouteSpecification(origin, destination, arrivalDeadline);

    final Cargo cargo = new Cargo(trackingId, routeSpecification);
    cargoRepository.store(cargo);
    
    logger.info("Booked new cargo with tracking id " + cargo.trackingId().stringValue());

    return cargo.trackingId();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Itinerary> requestPossibleRoutesForCargo(final TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);

    if (cargo == null) {
      return Collections.emptyList();
    }

    return routingService.fetchRoutesForSpecification(cargo.routeSpecification());
  }

  @Override
  @Transactional
  public void assignCargoToRoute(final Itinerary itinerary, final TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);
    Validate.notNull(cargo, "Can't assign itinerary to non-existing cargo " + trackingId);
    cargo.assignToRoute(itinerary);
    cargoRepository.store(cargo);

    logger.info("Assigned cargo " + trackingId + " to new route");
  }

  @Override
  @Transactional
  public void changeDestination(final TrackingId trackingId, final UnLocode unLocode) {
    final Cargo cargo = cargoRepository.find(trackingId);
    Validate.notNull(cargo, "Can't change destination of non-existing cargo " + trackingId);
    final Location newDestination = locationRepository.find(unLocode);

    final RouteSpecification routeSpecification = cargo.routeSpecification().withDestination(newDestination);
    cargo.specifyNewRoute(routeSpecification);

    cargoRepository.store(cargo);
    logger.info("Changed destination for cargo " + trackingId + " to " + routeSpecification.destination());
  }

  @Override
  @Transactional(readOnly = true)
  public Cargo loadCargoForRouting(final TrackingId trackingId) {
    return cargoRepository.find(trackingId);
  }

}
