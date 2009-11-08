package se.citerus.dddsample.tracking.core.domain.model.cargo;

import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;

import java.util.Date;

public class CargoFactory {

  private final LocationRepository locationRepository;
  private final TrackingIdFactory trackingIdFactory;

  public CargoFactory(LocationRepository locationRepository, TrackingIdFactory trackingIdFactory) {
    this.locationRepository = locationRepository;
    this.trackingIdFactory = trackingIdFactory;
  }

  public Cargo newCargo(UnLocode originUnLocode, UnLocode destinationUnLocode, Date arrivalDeadline) {
    final TrackingId trackingId = trackingIdFactory.nextTrackingId();
    final Location origin = locationRepository.find(originUnLocode);
    final Location destination = locationRepository.find(destinationUnLocode);
    final RouteSpecification routeSpecification = new RouteSpecification(origin, destination, arrivalDeadline);

    return new Cargo(trackingId, routeSpecification);
  }

}
