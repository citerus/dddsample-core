package se.citerus.dddsample.domain.service;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

/**
 * Cargo tracking service.
 *
 */
public interface TrackingService {

  /**
   * Track a particular cargo.
   *
   * @param trackingId cargo tracking id
   * @return A cargo and its delivery history, or null if no cargo with given tracking id is found.
   */
  Cargo track(TrackingId trackingId);

  /**
   * Inspect cargo and send relevant notifications to interested parties,
   * for example if a cargo has been misdirected, or unloaded
   * at the final destination.
   *
   * @param trackingId cargo tracking id
   */
  void inspectCargo(TrackingId trackingId);

}
