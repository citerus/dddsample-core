package se.citerus.dddsample.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.util.List;

public class CargoServiceImpl implements CargoService {
  private CargoRepository cargoRepository;
  private static final Log logger = LogFactory.getLog(CargoServiceImpl.class);

  @Transactional(readOnly = true)
  public CargoWithHistoryDTO track(TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      return null;
    }

    DeliveryHistory deliveryHistory = cargo.deliveryHistory();

    //CargoWithHistoryDTO

    Location currentLocation = deliveryHistory.currentLocation();
    CarrierMovement currentCarrierMovement = deliveryHistory.currentCarrierMovement();
    final CargoWithHistoryDTO dto = new CargoWithHistoryDTO(
            cargo.trackingId().idString(),
            cargo.origin().toString(),
            cargo.finalDestination().toString(),
            deliveryHistory.status(),
            currentLocation == null ? null : currentLocation.unLocode().idString(),
            currentCarrierMovement == null ? null : currentCarrierMovement.carrierId().idString()
    );

    final List<HandlingEvent> events = deliveryHistory.eventsOrderedByCompletionTime();
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

  @Transactional(readOnly = true)
  public void notifyIfMisdirected(TrackingId trackingId) {
    Cargo cargo = cargoRepository.find(trackingId);
    if (cargo.isMisdirected()) {
      // TODO: more elaborate notification than logging - mail, xmpp, other?
      logger.info("Cargo " + trackingId + " has been misdirected. " +
                  "Last event was " + cargo.deliveryHistory().lastEvent());
    }
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

}
