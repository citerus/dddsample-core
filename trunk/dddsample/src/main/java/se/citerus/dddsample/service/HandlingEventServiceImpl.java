package se.citerus.dddsample.service;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;

import java.util.Date;

public class HandlingEventServiceImpl implements HandlingEventService {
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;

  @Transactional(readOnly = false)
  public void register(Date completionTime, TrackingId trackingId, CarrierMovementId carrierMovementId, String unlocode, HandlingEvent.Type type) throws UnknownCarrierMovementIdException, UnknownTrackingIdException {
    Cargo cargo = findCargo(trackingId);
    CarrierMovement carrierMovement = findCarrierMovement(carrierMovementId);
    Location location = findLocation(unlocode);
    Date registrationTime = new Date();
    HandlingEvent event;
    if (carrierMovement != null) {
      event = new HandlingEvent(cargo, completionTime, registrationTime, type, carrierMovement);
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
    Validate.notNull(carrierMovementId, "Carrier ID is required");
    CarrierMovement carrierMovement = carrierMovementRepository.find(carrierMovementId);
    if (carrierMovement == null) {
      throw new UnknownCarrierMovementIdException(carrierMovementId);
    }

    return carrierMovement;
  }

  private Location findLocation(String unlocode) {
    // TODO: introdcue Location repository, lookup and add new Location when not found
    return new Location(unlocode);
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
}
