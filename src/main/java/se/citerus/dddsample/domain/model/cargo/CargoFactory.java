package se.citerus.dddsample.domain.model.cargo;

import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.Date;

public class CargoFactory {
    private final LocationRepository locationRepository;
    private final CargoRepository cargoRepository;

    public CargoFactory(LocationRepository locationRepository, CargoRepository cargoRepository) {
        this.locationRepository = locationRepository;
        this.cargoRepository = cargoRepository;
    }

    public Cargo createCargo(UnLocode originUnLoCode, UnLocode destinationUnLoCode, Date arrivalDeadline) {
        final TrackingId trackingId = cargoRepository.nextTrackingId();
        final Location origin = locationRepository.find(originUnLoCode);
        final Location destination = locationRepository.find(destinationUnLoCode);
        final RouteSpecification routeSpecification = new RouteSpecification(origin, destination, arrivalDeadline);

        return new Cargo(trackingId, routeSpecification);
    }
}
