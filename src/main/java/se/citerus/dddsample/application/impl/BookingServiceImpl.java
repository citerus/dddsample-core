package se.citerus.dddsample.application.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.application.BookingService;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BookingServiceImpl implements BookingService {

  private final CargoRepository cargoRepository;
  private final LocationRepository locationRepository;
  private final RoutingService routingService;
  private final CargoFactory cargoFactory;
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public BookingServiceImpl(final CargoRepository cargoRepository,
                            final LocationRepository locationRepository,
                            final RoutingService routingService,
                            final CargoFactory cargoFactory) {
    this.cargoRepository = cargoRepository;
    this.locationRepository = locationRepository;
    this.routingService = routingService;
    this.cargoFactory = cargoFactory;
  }

  @Override
  @Transactional
  public TrackingId bookNewCargo(final UnLocode originUnLocode,
                                 final UnLocode destinationUnLocode,
                                 final Date arrivalDeadline) {
    Cargo cargo = cargoFactory.createCargo(originUnLocode, destinationUnLocode, arrivalDeadline);

    cargoRepository.store(cargo);
    logger.info("Booked new cargo with tracking id {}", cargo.trackingId().idString());

    return cargo.trackingId();
  }

  @Override
  @Transactional
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
    if (cargo == null) {
      throw new IllegalArgumentException("Can't assign itinerary to non-existing cargo " + trackingId);
    }

    cargo.assignToRoute(itinerary);
    cargoRepository.store(cargo);

    logger.info("Assigned cargo {} to new route", trackingId);
  }

  @Override
  @Transactional
  public void changeDestination(final TrackingId trackingId, final UnLocode unLocode) {
    final Cargo cargo = cargoRepository.find(trackingId);
    final Location newDestination = locationRepository.find(unLocode);

    final RouteSpecification routeSpecification = new RouteSpecification(
      cargo.origin(), newDestination, cargo.routeSpecification().arrivalDeadline()
    );
    cargo.specifyNewRoute(routeSpecification);

    cargoRepository.store(cargo);
    logger.info("Changed destination for cargo {} to {}", trackingId, routeSpecification.destination());
  }

}
