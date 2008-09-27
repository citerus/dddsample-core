package se.citerus.dddsample.domain.model.handling;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.UnknownCarrierMovementIdException;
import se.citerus.dddsample.domain.service.UnknownLocationException;
import se.citerus.dddsample.domain.service.UnknownTrackingIdException;

import java.util.Date;

/**
 * Creates handling events.
 */
public class HandlingEventFactory {

  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private LocationRepository locationRepository;

  /**
   * @param completionTime    when the event was completed, for example finished loading
   * @param trackingId        tracking id
   * @param carrierMovementId carrier movement id, if applicable (may be null)
   * @param unlocode          United Nations Location Code for the location of the event
   * @param type              type of event
   * @throws UnknownCarrierMovementIdException
   *                                    if there's not carrier movement with this id
   * @throws UnknownTrackingIdException if there's no cargo with this tracking id
   * @throws UnknownLocationException   if there's no location with this UN Locode
   * @return A handling event.
   */
  public HandlingEvent createHandlingEvent(Date completionTime, TrackingId trackingId, CarrierMovementId carrierMovementId, UnLocode unlocode, HandlingEvent.Type type)
    throws UnknownTrackingIdException, UnknownCarrierMovementIdException, UnknownLocationException {

    // Carrier movement may be null for certain event types
    Validate.noNullElements(new Object[]{trackingId, unlocode, type});

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) throw new UnknownTrackingIdException(trackingId);

    final CarrierMovement carrierMovement = findCarrierMovement(carrierMovementId);

    final Location location = findLocation(unlocode);
    if (location == null) throw new UnknownLocationException(unlocode);

    final Date registrationTime = new Date();

    if (carrierMovement == null) {
      return new HandlingEvent(cargo, completionTime, registrationTime, type, location);   
    } else {
      return new HandlingEvent(cargo, completionTime, registrationTime, type, location, carrierMovement);
    }
  }

  private CarrierMovement findCarrierMovement(final CarrierMovementId carrierMovementId)
    throws UnknownCarrierMovementIdException {

    if (carrierMovementId == null) {
      return null;
    }
    final CarrierMovement carrierMovement = carrierMovementRepository.find(carrierMovementId);
    if (carrierMovement == null) {
      throw new UnknownCarrierMovementIdException(carrierMovementId);
    }

    return carrierMovement;
  }

  private Location findLocation(final UnLocode unlocode) throws UnknownLocationException {
    if (unlocode == null) {
      return Location.UNKNOWN;
    }

    final Location location = locationRepository.find(unlocode);
    if (location == null) {
      throw new UnknownLocationException(unlocode);
    }

    return location;
  }

  public void setCargoRepository(final CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setCarrierMovementRepository(final CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }

  public void setLocationRepository(final LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }


}
