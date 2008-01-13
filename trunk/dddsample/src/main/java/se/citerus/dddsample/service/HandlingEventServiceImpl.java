package se.citerus.dddsample.service;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HandlingEventServiceImpl implements HandlingEventService {
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;


  public void register(Date date, String type, String carrierId, String[] trackingIds) {
    CarrierMovement cm = findCarrier(new CarrierId(carrierId));
    HandlingEvent event = new HandlingEvent(date, HandlingEvent.parseType(type), cm);
    Set<Cargo> cargos = findCargos(trackingIds);
    event.register(cargos);
    
    handlingEventRepository.save(event);
  }


  private Set<Cargo> findCargos(String[] trackingIds) {
    Set<Cargo> cargos = new HashSet<Cargo>();
    
    for (String trackingId : trackingIds) {
      cargos.add(findCargo(trackingId));
    }
    
    return cargos;
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
