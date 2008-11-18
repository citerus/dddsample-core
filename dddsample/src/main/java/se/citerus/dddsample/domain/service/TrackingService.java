package se.citerus.dddsample.domain.service;

import se.citerus.dddsample.domain.model.cargo.TrackingId;

/**
 * Cargo tracking service.
 *
 */
public interface TrackingService {

  /**
   * Inspect cargo and send relevant notifications to interested parties,
   * for example if a cargo has been misdirected, or unloaded
   * at the final destination.
   *
   * @param trackingId cargo tracking id
   */
  void onCargoHandled(TrackingId trackingId);

}
