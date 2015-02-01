package se.citerus.dddsample.domain.model.handling;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

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
   * @param registrationTime  time when this event was received by the system
   * @param completionTime    when the event was completed, for example finished loading
   * @param trackingId        cargo tracking id
   * @param voyageNumber      voyage number
   * @param unlocode          United Nations Location Code for the location of the event
   * @param type              type of event
   * @throws UnknownVoyageException   if there's no voyage with this number
   * @throws UnknownCargoException    if there's no cargo with this tracking id
   * @throws UnknownLocationException if there's no location with this UN Locode
   * @return A handling event.
   */
  public HandlingEvent createHandlingEvent(Date registrationTime, Date completionTime, TrackingId trackingId, VoyageNumber voyageNumber, UnLocode unlocode, HandlingEvent.Type type)
    throws CannotCreateHandlingEventException {
    final Cargo cargo = findCargo(trackingId);
    final Voyage voyage = findVoyage(voyageNumber);
    final Location location = findLocation(unlocode);

    try {
      if (voyage == null) {
        return new HandlingEvent(cargo, completionTime, registrationTime, type, location);
      } else {
        return new HandlingEvent(cargo, completionTime, registrationTime, type, location, voyage);
      }
    } catch (Exception e) {
      throw new CannotCreateHandlingEventException(e);
    }
  }

  private Cargo findCargo(TrackingId trackingId) throws UnknownCargoException {
    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) throw new UnknownCargoException(trackingId);
    return cargo;
  }

  private Voyage findVoyage(VoyageNumber voyageNumber) throws UnknownVoyageException {
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
