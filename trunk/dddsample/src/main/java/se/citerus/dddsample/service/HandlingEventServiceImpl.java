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
  public void registerLoad(Date completionTime, String carrierMovementId, String[] trackingIds) {
    doRegisterWithCarrierMovement(completionTime, findCarrierMovement(carrierMovementId), HandlingEvent.Type.LOAD, trackingIds);
  }

  @Transactional(readOnly = false)
  public void registerUnload(Date completionTime, String carrierMovementId, String[] trackingIds) {
    doRegisterWithCarrierMovement(completionTime, findCarrierMovement(carrierMovementId), HandlingEvent.Type.UNLOAD, trackingIds);
  }

  @Transactional(readOnly = false)
  public void registerClaim(Date completionTime, String unlocode, String[] trackingIds) {
    doRegisterWithLocation(completionTime, findLocation(unlocode), HandlingEvent.Type.CLAIM, trackingIds);
  }

  @Transactional(readOnly = false)
  public void registerRecieve(Date completionTime, String unlocode, String[] trackingIds) {
    doRegisterWithLocation(completionTime, findLocation(unlocode), HandlingEvent.Type.RECEIVE, trackingIds);
  }

  @Transactional(readOnly = false)
  public void registerCustomsCleared(Date completionTime, String unlocode, String[] trackingIds) {
    doRegisterWithLocation(completionTime, findLocation(unlocode), HandlingEvent.Type.CUSTOMS, trackingIds);
  }

  private void doRegisterWithLocation(Date completionTime, Location location, HandlingEvent.Type type, String[] trackingIds) {
    for (String tid: trackingIds) {
      Cargo cargo = findCargo(tid);
      Date registrationTime = new Date();
      HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, type, location);
      handlingEventRepository.save(event);
    }
  }

  private void doRegisterWithCarrierMovement(Date completionTime, CarrierMovement carrierMovement, HandlingEvent.Type type, String[] trackingIds) {
    for (String tid: trackingIds) {
      Cargo cargo = findCargo(tid);
      Date registrationTime = new Date();
      HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, type, carrierMovement);
      handlingEventRepository.save(event);
    }
  }

  private Cargo findCargo(String trackingId) {
    Validate.notNull(trackingId, "Tracking ID is required");
    Cargo cargo = cargoRepository.find(new TrackingId(trackingId));
    Validate.notNull(cargo, "Cargo is not found. Tracking ID=" + trackingId);

    return cargo;
  }

  private CarrierMovement findCarrierMovement(String carrierId) {
    Validate.notNull(carrierId, "Carrier ID is required");
    CarrierMovement carrier = carrierMovementRepository.find(new CarrierMovementId(carrierId));
    Validate.notNull(carrier, "Carrier is not found: Carrier ID=" + carrierId);

    return carrier;
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
