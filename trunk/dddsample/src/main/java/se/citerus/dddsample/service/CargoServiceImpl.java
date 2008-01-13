package se.citerus.dddsample.service;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.util.List;

public class CargoServiceImpl implements CargoService {
  private CargoRepository cargoRepository;

  @Transactional(readOnly = true)
  public CargoWithHistoryDTO find(String trackingId) {
    final TrackingId tid = new TrackingId(trackingId);
    final Cargo cargo = cargoRepository.find(tid);
    if (cargo == null) {
      return null;
    }
    final CargoWithHistoryDTO dto = new CargoWithHistoryDTO(
            cargo.trackingId().getId(),
            cargo.origin().unlocode(),
            cargo.finalDestination().unlocode(),
            cargo.getCurrentLocation().unlocode()
    );
    final List<HandlingEvent> events = cargo.getDeliveryHistory().eventsOrderedByTime();
    for (HandlingEvent event : events) {
      CarrierMovement cm = event.getCarrierMovement();
      String carrierIdString =
              (cm == null) ? Location.UNKNOWN.unlocode() : cm.carrierId().getId();
      dto.addEvent(new HandlingEventDTO(
              event.getLocation().unlocode(),
              event.getType().toString(),
              carrierIdString,
              event.getTimeOccurred()
      ));
    }
    return dto;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

}
