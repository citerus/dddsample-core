package se.citerus.dddsample.application;

import se.citerus.dddsample.domain.model.cargo.TrackingId;

/**
 * Cargo tracking service.
 */
public interface TrackingService {

  /**
   * Inspect cargo and send relevant notifications to interested parties,
   * for example if a cargo has been misdirected, or unloaded
   * at the final destination.
   *
   * @param trackingId cargo tracking id
   */
  // TODO rename! The method updates the delivery status on handling
  // TODO [Cargo]InspectionService is fine 
  void inspectCargo(TrackingId trackingId);

}
