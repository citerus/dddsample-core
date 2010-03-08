package se.citerus.dddsample.tracking.core.domain.model.handling;

import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

import java.util.Date;

/**
 * Creates handling events.
 */
public class HandlingEventFactory {

  private final CargoRepository cargoRepository;
  private final VoyageRepository voyageRepository;
  private final LocationRepository locationRepository;

  public HandlingEventFactory(final CargoRepository cargoRepository,
                              final VoyageRepository voyageRepository,
                              final LocationRepository locationRepository) {
    this.cargoRepository = cargoRepository;
    this.voyageRepository = voyageRepository;
    this.locationRepository = locationRepository;
  }

  /**
   * @param completionTime   when the event was completed, for example finished loading
   * @param trackingId       cargo tracking id
   * @param voyageNumber     voyage number
   * @param unlocode         United Nations Location Code for the location of the event
   * @param type             type of event
   * @param operatorCode     operator code
   * @return A handling event.
   * @throws UnknownVoyageException   if there's no voyage with this number
   * @throws UnknownCargoException    if there's no cargo with this tracking id
   * @throws UnknownLocationException if there's no location with this UN Locode
   */
  public HandlingEvent createHandlingEvent(final Date completionTime, final TrackingId trackingId,
                                           final VoyageNumber voyageNumber, final UnLocode unlocode,
                                           final HandlingActivityType type, final OperatorCode operatorCode)
    throws CannotCreateHandlingEventException {

    final Cargo cargo = findCargo(trackingId);
    final Voyage voyage = findVoyage(voyageNumber);
    final Location location = findLocation(unlocode);

    try {
      final Date registrationTime = new Date();
      if (voyage == null) {
        return new HandlingEvent(cargo, completionTime, registrationTime, type, location);
      } else {
        return new HandlingEvent(cargo, completionTime, registrationTime, type, location, voyage, operatorCode);
      }
    } catch (Exception e) {
      throw new CannotCreateHandlingEventException(e);
    }
  }

  private Cargo findCargo(final TrackingId trackingId) throws UnknownCargoException {
    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) throw new UnknownCargoException(trackingId);
    return cargo;
  }

  private Voyage findVoyage(final VoyageNumber voyageNumber) throws UnknownVoyageException {
    if (voyageNumber == null) {
      return null;
    }

    final Voyage voyage = voyageRepository.find(voyageNumber);
    if (voyage == null) {
      throw new UnknownVoyageException(voyageNumber);
    }

    return voyage;
  }

  private Location findLocation(final UnLocode unlocode) throws UnknownLocationException {
    final Location location = locationRepository.find(unlocode);
    if (location == null) {
      throw new UnknownLocationException(unlocode);
    }

    return location;
  }

}
