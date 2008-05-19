package se.citerus.dddsample.service;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.LocationRepository;
import se.citerus.dddsample.service.dto.CargoRoutingDTO;
import se.citerus.dddsample.service.dto.CargoTrackingDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;
import se.citerus.dddsample.service.dto.LegDTO;

import java.util.ArrayList;
import java.util.List;

public class CargoServiceImpl implements CargoService {

  private CargoRepository cargoRepository;
  private LocationRepository locationRepository;
  private static final Log logger = LogFactory.getLog(CargoServiceImpl.class);

  @Transactional(readOnly = false)
  public TrackingId registerNew(UnLocode originUnLocode, UnLocode destinationUnLocode) {
    Validate.notNull(originUnLocode);
    Validate.notNull(destinationUnLocode);

    TrackingId trackingId = cargoRepository.nextTrackingId();
    Location origin = locationRepository.find(originUnLocode);
    Location destination = locationRepository.find(destinationUnLocode);

    Cargo cargo = new Cargo(trackingId,origin,destination);

    cargoRepository.save(cargo);
    logger.info("Registered new cargo with tracking id " + trackingId.idString());

    return trackingId;
  }

  @Transactional(readOnly = true)
  public List<String> shippingLocations() {
    List<Location> allLocations = locationRepository.findAll();
    List<String> unlocodes = new ArrayList<String>(allLocations.size());
    for (Location location : allLocations) {
      unlocodes.add(location.unLocode().idString());
    }
    return unlocodes;
  }

  @Transactional(readOnly = true)
  public CargoTrackingDTO track(TrackingId trackingId) {
    Validate.notNull(trackingId);

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      return null;
    }

    DeliveryHistory deliveryHistory = cargo.deliveryHistory();

    // TODO: use DTO assemblers
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
    Validate.notNull(trackingId);

    Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      logger.warn("Can't notify listeners for non-existing cargo " + trackingId);
      return;
    }

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

  @Transactional(readOnly = true)
  public List<CargoRoutingDTO> loadAllForRouting() {
    List<Cargo> allCargos = cargoRepository.findAll();

    // TODO: use DTO assembler
    List<CargoRoutingDTO> dtoList = new ArrayList<CargoRoutingDTO>(allCargos.size());
    for (Cargo cargo : allCargos) {
      CargoRoutingDTO dto = new CargoRoutingDTO(
        cargo.trackingId().idString(),
        cargo.origin().toString(),
        cargo.finalDestination().toString()
      );
      for (Leg leg : cargo.itinerary().legs()) {
        dto.addLeg(
          leg.carrierMovementId().idString(),
          leg.from().unLocode().idString(),
          leg.to().unLocode().idString()
        );
      }
      dtoList.add(dto);
    }

    return dtoList;
  }

  @Transactional(readOnly = true)
  public CargoRoutingDTO loadForRouting(TrackingId trackingId) {
    Validate.notNull(trackingId);
    Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      return null;
    }

    // TODO: use DTO assembler
    CargoRoutingDTO dto = new CargoRoutingDTO(
      cargo.trackingId().idString(),
      cargo.origin().toString(),
      cargo.finalDestination().toString()
    );
    for (Leg leg : cargo.itinerary().legs()) {
      dto.addLeg(
        leg.carrierMovementId().idString(),
        leg.from().toString(),
        leg.to().toString()
      );
    }
    return dto;
  }

  @Transactional(readOnly = false)
  public void assignItinerary(TrackingId trackingId, List<LegDTO> legDTOs) {
    Validate.notNull(trackingId);
    Validate.notNull(legDTOs);

    Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      throw new IllegalArgumentException("Can't assign itinerary to non-existing cargo " + trackingId);
    }

    List<Leg> legs = new ArrayList<Leg>(legDTOs.size());
    for (LegDTO legDTO : legDTOs) {
      legs.add(new Leg(
        new CarrierMovementId(legDTO.getCarrierMovementId()),
        locationRepository.find(new UnLocode(legDTO.getFrom())),
        locationRepository.find(new UnLocode(legDTO.getTo())))
      );
    }

    cargo.setItinerary(new Itinerary(legs));
    cargoRepository.save(cargo);
  }


  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }
}
