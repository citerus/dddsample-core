package se.citerus.dddsample.service;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.util.List;

public class CargoServiceImpl implements CargoService {
  private CargoRepository cargoRepository;

  @Transactional(readOnly = true)
  public CargoWithHistoryDTO track(String trackingId) {
    final TrackingId tid = new TrackingId(trackingId);
    final Cargo cargo = cargoRepository.find(tid);
    if (cargo == null) {
      return null;
    }

    HandlingEvent lastEvent = cargo.deliveryHistory().lastEvent();

    //CargoWithHistoryDTO

    String currentLocationId = null;
    String carrierMovementId = null;

    if (lastEvent.type() == HandlingEvent.Type.UNLOAD || lastEvent.type() == HandlingEvent.Type.RECEIVE)
      currentLocationId = cargo.lastKnownLocation().unLocode().idString();

    if (lastEvent.type() == HandlingEvent.Type.LOAD)
      carrierMovementId = lastEvent.carrierMovement().carrierId().idString();


    final CargoWithHistoryDTO dto = new CargoWithHistoryDTO(
            cargo.trackingId().idString(),
            cargo.origin().toString(),
            cargo.finalDestination().toString(),
            statusForLastEvent(lastEvent),
            currentLocationId,
            carrierMovementId
    );

    final List<HandlingEvent> events = cargo.deliveryHistory().eventsOrderedByCompletionTime();
    for (HandlingEvent event : events) {
      CarrierMovement cm = event.carrierMovement();
      String carrierIdString = (cm == null) ? "" : cm.carrierId().idString();
      dto.addEvent(new HandlingEventDTO(
              event.location().toString(),
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

  public CargoWithHistoryDTO.StatusCode statusForLastEvent(HandlingEvent lastEvent) {

    if (lastEvent == null)
      return CargoWithHistoryDTO.StatusCode.notReceived;

    HandlingEvent.Type type = lastEvent.type();
    if (type == HandlingEvent.Type.LOAD)
      return CargoWithHistoryDTO.StatusCode.onBoardCarrier;

    if (type == HandlingEvent.Type.UNLOAD)
      return CargoWithHistoryDTO.StatusCode.inPort;

    if (type == HandlingEvent.Type.RECEIVE)
      return CargoWithHistoryDTO.StatusCode.inPort;

    if (type == HandlingEvent.Type.CLAIM)
      return CargoWithHistoryDTO.StatusCode.claimed;


    return null;
  }
}
