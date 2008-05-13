package se.citerus.dddsample.service;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.service.dto.CargoTrackingDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.util.List;

public class CargoServiceImpl implements CargoService {

  private CargoRepository cargoRepository;
  private static final Log logger = LogFactory.getLog(CargoServiceImpl.class);

  @Transactional(readOnly = false)
  public TrackingId registerNew(Location origin, Location destination) {
    Validate.notNull(origin);
    Validate.notNull(destination);

    TrackingId trackingId = cargoRepository.nextTrackingId();
    Cargo cargo = new Cargo(trackingId, origin, destination);
    cargoRepository.save(cargo);

    logger.info("Registered new cargo with tracking id " + trackingId.idString());
    
    return trackingId;
  }

  @Transactional(readOnly = true)
  public CargoTrackingDTO track(TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      return null;
    }

    DeliveryHistory deliveryHistory = cargo.deliveryHistory();

    Location currentLocation = deliveryHistory.currentLocation();
    CarrierMovement currentCarrierMovement = deliveryHistory.currentCarrierMovement();
    final CargoTrackingDTO dto = new CargoTrackingDTO(
      cargo.trackingId().idString(),
      cargo.origin().toString(),
      cargo.finalDestination().toString(),
      deliveryHistory.status(),
      currentLocation == null ? null : currentLocation.unLocode().idString(),
      currentCarrierMovement == null ? null : currentCarrierMovement.carrierId().idString(),
      cargo.isMisdirected()
    );

    final List<HandlingEvent> events = deliveryHistory.eventsOrderedByCompletionTime();
    for (HandlingEvent event : events) {
      CarrierMovement cm = event.carrierMovement();
      String carrierIdString = (cm == null) ? "" : cm.carrierId().idString();
      dto.addEvent(new HandlingEventDTO(
        event.location().toString(),
        event.type().toString(),
        carrierIdString,
        event.completionTime(),
        cargo.itinerary().isExpected(event)
      ));
    }
    return dto;

  }

  // TODO: move this to another class?
  @Transactional(readOnly = true)
  public void notify(TrackingId trackingId) {
    Cargo cargo = cargoRepository.find(trackingId);
    // TODO: more elaborate notifications, such as email to affected customer
    if (cargo.isMisdirected()) {
      logger.info("Cargo " + trackingId + " has been misdirected. " +
                  "Last event was " + cargo.deliveryHistory().lastEvent());
    }
    if (cargo.isUnloadedAtDestination()) {
      logger.info("Cargo " + trackingId + " has been unloaded " +
                  "at its final destination " + cargo.finalDestination());
    }
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

}
