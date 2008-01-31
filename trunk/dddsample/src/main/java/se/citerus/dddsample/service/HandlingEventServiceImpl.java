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
  public void register(Date completionTime, TrackingId trackingId, CarrierMovementId carrierMovementId, String unlocode, HandlingEvent.Type type) throws UnknownCarrierMovementIdException, UnknownTrackingIdException, UnknownLocationException {
    Cargo cargo = findCargo(trackingId);
    CarrierMovement carrierMovement = findCarrierMovement(carrierMovementId);
    Location location = findLocation(unlocode);
    Date registrationTime = new Date();
    HandlingEvent event;
    if (carrierMovement != null) {
      event = new HandlingEvent(cargo, completionTime, registrationTime, type, location, carrierMovement);
    } else {
      event = new HandlingEvent(cargo, completionTime, registrationTime, type, location);
    }
    handlingEventRepository.save(event);
  }

  private Cargo findCargo(TrackingId trackingId) throws UnknownTrackingIdException {
    Validate.notNull(trackingId, "Tracking ID is required");
    Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      throw new UnknownTrackingIdException(trackingId);
    }

    return cargo;
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

  private Location findLocation(String unlocode) throws UnknownLocationException {
    if (unlocode == null){
      return Location.UNKNOWN;
    }
    
    Location location = locationRepository.find(unlocode);
    if (location == null){
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
