package se.citerus.dddsample.service;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.util.List;

public class CargoServiceImpl implements CargoService {
  private CargoRepository cargoRepository;
  private HandlingEventRepository handlingEventRepository;

  @Transactional(readOnly = true)
  public CargoWithHistoryDTO find(String trackingId) {
    final TrackingId tid = new TrackingId(trackingId);
    final Cargo cargo = cargoRepository.find(tid);
    if (cargo == null) {
      return null;
    }
    DeliveryHistory deliveryHistory = handlingEventRepository.findDeliveryHistory(tid);
    HandlingEvent lastEvent = deliveryHistory.lastEvent();
    String currentLocation;
    if (lastEvent != null) {
      currentLocation = lastEvent.location().unlocode();
    } else {
      currentLocation = "";
    }
    final CargoWithHistoryDTO dto = new CargoWithHistoryDTO(
            cargo.trackingId().idString(),
            cargo.origin().unlocode(),
            cargo.finalDestination().unlocode(),
            currentLocation
    );
    final List<HandlingEvent> events = deliveryHistory.eventsOrderedByTime();
    for (HandlingEvent event : events) {
      CarrierMovement cm = event.carrierMovement();
      String carrierIdString = (cm == null) ? "" : cm.carrierId().idString();
      dto.addEvent(new HandlingEventDTO(
              event.location().unlocode(),
              event.type().toString(),
              carrierIdString,
              event.completionTime()
      ));
    }
    return dto;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }

}
