package se.citerus.dddsample.service;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;
import se.citerus.dddsample.repository.LocationRepository;

import java.util.Date;

public class HandlingEventServiceImpl implements HandlingEventService {
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;

  @Transactional(readOnly = false)
  public void register(Date completionTime, TrackingId trackingId, CarrierMovementId carrierMovementId, UnLocode unlocode, HandlingEvent.Type type) throws UnknownCarrierMovementIdException, UnknownTrackingIdException, UnknownLocationException {
    // Carrier movement may be null for certain event types
    Validate.noNullElements(new Object[] {trackingId, unlocode, type});

    Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) throw new UnknownTrackingIdException(trackingId);

    CarrierMovement carrierMovement = findCarrierMovement(carrierMovementId);
    Location location = findLocation(unlocode);
    Date registrationTime = new Date();
    HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, type, location, carrierMovement);

    handlingEventRepository.save(event);

    /*
      NOTE:
        The cargo instance that's loaded and associated with the handling event is
        in an inconsitent state, because the cargo delivery history's collection of
        events does not contain the event created here. However, this is not a problem,
        because cargo is in a different aggregate from handling event.
        The rules of an aggregate dictate that all consistency rules within the aggregate
        are enforced synchronously in the transaction, but consistency rules of other aggregates
        are enforced by asynchronous updates, after the commit of this transaction.
     */
  }

  private CarrierMovement findCarrierMovement(CarrierMovementId carrierMovementId) throws UnknownCarrierMovementIdException {
    if (carrierMovementId == null) {
      return null;
    }
    CarrierMovement carrierMovement = carrierMovementRepository.find(carrierMovementId);
    if (carrierMovement == null) {
      throw new UnknownCarrierMovementIdException(carrierMovementId);
    }

    return carrierMovement;
  }

  private Location findLocation(UnLocode unlocode) throws UnknownLocationException {
    if (unlocode == null) {
      return Location.UNKNOWN;
    }
    
    Location location = locationRepository.find(unlocode);
    if (location == null) {
      throw new UnknownLocationException(unlocode);
    }
    
    return location;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setCarrierRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }
}
