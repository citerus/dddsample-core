package se.citerus.dddsample.domain.model.handling;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.Date;

/**
 * Creates handling events.
 */
public class HandlingEventFactory {

  private final CargoRepository cargoRepository;
  private final VoyageRepository voyageRepository;
  private final LocationRepository locationRepository;

  public HandlingEventFactory(CargoRepository cargoRepository, VoyageRepository voyageRepository, LocationRepository locationRepository) {
    this.cargoRepository = cargoRepository;
    this.voyageRepository = voyageRepository;
    this.locationRepository = locationRepository;
  }

  /**
   * @param completionTime    when the event was completed, for example finished loading
   * @param trackingId        tracking id
   * @param voyageNumber      voyage number
   * @param unlocode          United Nations Location Code for the location of the event
   * @param type              type of event
   * @throws UnknownVoyageException
   *                                    if there's not carrier movement with this id
   * @throws UnknownCargoException if there's no cargo with this tracking id
   * @throws UnknownLocationException   if there's no location with this UN Locode
   * @return A handling event.
   */
  public HandlingEvent createHandlingEvent(Date completionTime, TrackingId trackingId, VoyageNumber voyageNumber, UnLocode unlocode, HandlingEvent.Type type)
    throws CannotCreateHandlingEventException {

    // Voyage number may be null for certain event types
    Validate.noNullElements(new Object[]{completionTime, trackingId, unlocode, type});

    final Cargo cargo = findCargo(trackingId);
    final Voyage voyage = findVoyage(voyageNumber);
    final Location location = findLocation(unlocode);
    
    if (location == null) throw new UnknownLocationException(unlocode);

    // TODO parameterize
    final Date registrationTime = new Date();

    if (voyage == null) {
      return new HandlingEvent(cargo, completionTime, registrationTime, type, location);   
    } else {
      return new HandlingEvent(cargo, completionTime, registrationTime, type, location, voyage);
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
