package se.citerus.dddsample.service;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Transactional;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.CarrierId;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;

public class HandlingEventServiceImpl implements HandlingEventService {
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;


  @Transactional(readOnly = false)
  public void register(Date date, String type, Location location, String carrierId, String trackingId) {
    CarrierMovement cm = findCarrier(new CarrierId(carrierId));
    HandlingEvent event = new HandlingEvent(date, new Date(), HandlingEvent.parseType(type), location, cm);
    Cargo cargo = findCargo(trackingId);
    event.add(cargo);
    
    handlingEventRepository.save(event);
  }

  private Cargo findCargo(String trackingId) {
    Cargo cargo = cargoRepository.find(new TrackingId(trackingId));
    Validate.notNull(cargo, "Cargo is not found. Tracking ID=" + trackingId);
    
    return cargo;
  }

  private CarrierMovement findCarrier(CarrierId carrierId) {
    if (carrierId == null){
      return null;
    }
    CarrierMovement carrier = carrierMovementRepository.find(carrierId);
    Validate.notNull(carrier, "Carrier is not found: Carrier ID=" + carrierId);
    
    return carrier;
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
