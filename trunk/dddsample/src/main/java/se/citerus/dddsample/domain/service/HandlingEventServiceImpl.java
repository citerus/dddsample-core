package se.citerus.dddsample.domain.service;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.Date;

public final class HandlingEventServiceImpl implements HandlingEventService {
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;
  private EventService eventService;

  @Transactional(readOnly = false)
  public void register(final Date completionTime, final TrackingId trackingId, final CarrierMovementId carrierMovementId,
                       final UnLocode unlocode, final HandlingEvent.Type type)
    throws UnknownCarrierMovementIdException, UnknownTrackingIdException, UnknownLocationException {

    // Carrier movement may be null for certain event types
    Validate.noNullElements(new Object[]{trackingId, unlocode, type});

    Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) throw new UnknownTrackingIdException(trackingId);

    final CarrierMovement carrierMovement = findCarrierMovement(carrierMovementId);
    final Location location = findLocation(unlocode);
    final Date registrationTime = new Date();

    final HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, type, location, carrierMovement);

    /*
      NOTE:
        The cargo instance that's loaded and associated with the handling event is
        in an inconsitent state, because the cargo delivery history's collection of
        events does not contain the event created here. However, this is not a problem,
        because cargo is in a different aggregate from handling event.

        The rules of an aggregate dictate that all consistency rules within the aggregate
        are enforced synchronously in the transaction, but consistency rules of other aggregates
        are enforced by asynchronous updates, after the commit of this transaction.
     */
    handlingEventRepository.save(event);

    eventService.fireHandlingEventRegistered(event);
  }

  private CarrierMovement findCarrierMovement(final CarrierMovementId carrierMovementId)
    throws UnknownCarrierMovementIdException {

    if (carrierMovementId == null) {
      return null;
    }
    final CarrierMovement carrierMovement = carrierMovementRepository.find(carrierMovementId);
    if (carrierMovement == null) {
      throw new UnknownCarrierMovementIdException(carrierMovementId);
    }

    return carrierMovement;
  }

  private Location findLocation(final UnLocode unlocode) throws UnknownLocationException {
    if (unlocode == null) {
      return Location.UNKNOWN;
    }

    final Location location = locationRepository.find(unlocode);
    if (location == null) {
      throw new UnknownLocationException(unlocode);
    }

    return location;
  }

  public void setCargoRepository(final CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setCarrierMovementRepository(final CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }

  public void setHandlingEventRepository(final HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }

  public void setLocationRepository(final LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setEventService(final EventService eventService) {
    this.eventService = eventService;
  }
}
