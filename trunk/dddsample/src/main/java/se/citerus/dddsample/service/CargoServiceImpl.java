package se.citerus.dddsample.service;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.LocationRepository;
import se.citerus.dddsample.service.dto.*;
import se.citerus.dddsample.service.dto.assembler.CargoRoutingDTOAssembler;
import se.citerus.dddsample.service.dto.assembler.CargoTrackingDTOAssembler;

import java.util.ArrayList;
import java.util.List;

public final class CargoServiceImpl implements CargoService {

  private CargoRepository cargoRepository;
  private LocationRepository locationRepository;
  private CarrierMovementRepository carrierMovementRepository;

  private final Log logger = LogFactory.getLog(getClass());

  @Transactional(readOnly = false)
  public TrackingId registerNew(final UnLocode originUnLocode, final UnLocode destinationUnLocode) {
    Validate.notNull(originUnLocode);
    Validate.notNull(destinationUnLocode);

    final TrackingId trackingId = cargoRepository.nextTrackingId();
    final Location origin = locationRepository.find(originUnLocode);
    final Location destination = locationRepository.find(destinationUnLocode);

    final Cargo cargo = new Cargo(trackingId, origin, destination);

    cargoRepository.save(cargo);
    logger.info("Registered new cargo with tracking id " + trackingId.idString());

    return trackingId;
  }

  @Transactional(readOnly = true)
  public List<UnLocode> shippingLocations() {
    final List<Location> allLocations = locationRepository.findAll();
    final List<UnLocode> unlocodes = new ArrayList<UnLocode>(allLocations.size());
    for (Location location : allLocations) {
      unlocodes.add(location.unLocode());
    }
    return unlocodes;
  }

  @Transactional(readOnly = true)
  public CargoTrackingDTO track(final TrackingId trackingId) {
    Validate.notNull(trackingId);

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      return null;
    }

    return new CargoTrackingDTOAssembler().toDTO(cargo);
  }

  // TODO: move this to another class?
  @Transactional(readOnly = true)
  public void notify(final TrackingId trackingId) {
    Validate.notNull(trackingId);

    final Cargo cargo = cargoRepository.find(trackingId);
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
    final List<Cargo> allCargos = cargoRepository.findAll();

    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();
    final List<CargoRoutingDTO> dtoList = new ArrayList<CargoRoutingDTO>(allCargos.size());

    for (Cargo cargo : allCargos) {
      dtoList.add(assembler.toDTO(cargo));
    }

    return dtoList;
  }

  @Transactional(readOnly = true)
  public CargoRoutingDTO loadForRouting(final TrackingId trackingId) {
    Validate.notNull(trackingId);
    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      return null;
    }

    return new CargoRoutingDTOAssembler().toDTO(cargo);
  }

  @Transactional(readOnly = false)
  public void assignItinerary(final TrackingId trackingId, final ItineraryCandidateDTO itinerary) {
    Validate.notNull(trackingId);
    Validate.notNull(itinerary);

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      throw new IllegalArgumentException("Can't assign itinerary to non-existing cargo " + trackingId);
    }

    final List<Leg> legs = new ArrayList<Leg>(itinerary.getLegs().size());
    for (LegDTO legDTO : itinerary.getLegs()) {
      final CarrierMovementId carrierMovementId = new CarrierMovementId(legDTO.getCarrierMovementId());
      final CarrierMovement carrierMovement = carrierMovementRepository.find(carrierMovementId);
      final Location from = locationRepository.find(new UnLocode(legDTO.getFrom()));
      final Location to = locationRepository.find(new UnLocode(legDTO.getTo()));
      legs.add(new Leg(carrierMovement, from, to));
    }
    // TODO: delete orphaned itineraries.
    // Can't cascade delete-orphan for many-to-one using mapping directives.
    cargo.setItinerary(new Itinerary(legs));
    cargoRepository.save(cargo);
  }

  public void setCargoRepository(final CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setLocationRepository(final LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }
}
