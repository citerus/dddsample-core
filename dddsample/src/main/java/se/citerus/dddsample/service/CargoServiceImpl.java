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
            cargo.trackingId().toString(),
            cargo.origin().unlocode(),
            cargo.finalDestination().unlocode(),
            cargo.currentLocation().unlocode()
    );
    final List<HandlingEvent> events = cargo.eventsOrderedByTime();
    for (HandlingEvent event : events) {
      CarrierMovement cm = event.carrierMovement();
      String carrierIdString =
              (cm == null) ? Location.UNKNOWN.unlocode() : cm.carrierId().toString();
      dto.addEvent(new HandlingEventDTO(
              event.location().unlocode(),
              event.type().toString(),
              carrierIdString,
              event.timeOccurred()
      ));
    }
    return dto;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

}
