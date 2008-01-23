package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.CarrierMovementId;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;

import java.util.Date;


/**
 * Handling event service.
 *
 */
public interface HandlingEventService {

  /**
   * @param completionTime when the event was completed, for example finished loading
   * @param trackingId tracking id
   * @param carrierMovementId carrier movement id, if applicable (may be null)
   * @param unlocode United Nations Location Code for the location of the event
   * @param type type of event
   * @throws UnknownCarrierMovementIdException if there's not carrier movement with this id
   * @throws UnknownTrackingIdException if there's no cargo with this tracking id
   */
  void register(Date completionTime, TrackingId trackingId, CarrierMovementId carrierMovementId, String unlocode, HandlingEvent.Type type)
  throws UnknownCarrierMovementIdException, UnknownTrackingIdException;

}