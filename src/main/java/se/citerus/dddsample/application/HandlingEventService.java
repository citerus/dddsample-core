package se.citerus.dddsample.application;

import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.util.Date;

/**
 * Handling event service.
 */
public interface HandlingEventService {

  /**
   * Registers a handling event in the system, and notifies interested
   * parties that a cargo has been handled.
   *
   * @param completionTime when the event was completed
   * @param trackingId cargo tracking id
   * @param voyageNumber voyage number
   * @param unLocode UN locode for the location where the event occurred
   * @param type type of event
   * @throws se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException
   *  if a handling event that represents an actual event that's relevant to a cargo we're tracking
   *  can't be created from the parameters 
   */
  void registerHandlingEvent(Date completionTime,
                             TrackingId trackingId,
                             VoyageNumber voyageNumber,
                             UnLocode unLocode,
                             HandlingEvent.Type type) throws CannotCreateHandlingEventException;

}
