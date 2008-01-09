package se.citerus.dddsample.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;

public class HandlingEventServiceImpl implements HandlingEventService {
  private CargoRepository cargoRepository;
  private CarrierRepository carrierRepository;
  private HandlingEventRepository handlingEventRepository;


  public void register(Date date, String type, String carrierId, String[] trackingIds) {
    HandlingEvent event = new HandlingEvent(date, HandlingEvent.parseType(type), findCarrier(carrierId));
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

  private CarrierMovement findCarrier(String carrierId) {
    if (carrierId == null){
      return null;
    }
    CarrierMovement carrier = carrierRepository.find(carrierId);
    Validate.notNull(carrier, "Carrier is not found: Carrier ID=" + carrierId);
    
    return carrier;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setCarrierRepository(CarrierRepository carrierRepository) {
    this.carrierRepository = carrierRepository;
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }



}
