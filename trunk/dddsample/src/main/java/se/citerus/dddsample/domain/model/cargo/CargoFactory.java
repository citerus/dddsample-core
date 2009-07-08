/**
 * Purpose
 * @author peter
 * @created 2009-jun-14
 * $Id$
 */
package se.citerus.dddsample.domain.model.cargo;

import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.Date;

public class CargoFactory {

  private final CargoRepository cargoRepository;
  private final LocationRepository locationRepository;

  public CargoFactory(CargoRepository cargoRepository, LocationRepository locationRepository) {
    this.cargoRepository = cargoRepository;
    this.locationRepository = locationRepository;
  }

  public Cargo newCargo(UnLocode originUnLocode, UnLocode destinationUnLocode, Date arrivalDeadline) {
    final TrackingId trackingId = cargoRepository.nextTrackingId();
    final Location origin = locationRepository.find(originUnLocode);
    final Location destination = locationRepository.find(destinationUnLocode);
    final RouteSpecification routeSpecification = new RouteSpecification(origin, destination, arrivalDeadline);

    return new Cargo(trackingId, routeSpecification);
  }

}
