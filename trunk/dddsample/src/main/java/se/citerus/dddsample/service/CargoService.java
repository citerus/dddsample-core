package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.service.dto.CargoTrackingDTO;

/**
 * Cargo service.
 *
 */
public interface CargoService {

  /**
   * Registers a new cargo in the tracking system, not yet routed.
   *
   * @param origin cargo origin
   * @param destination cargo destination
   * @return Cargo tracking id
   */
  TrackingId registerNew(Location origin, Location destination);

  /**
   * @param trackingId tracking id
   * @return A cargo and its delivery history, or null if no cargo with given tracking id is found.
   */
  CargoTrackingDTO track(TrackingId trackingId);

  /**
   * Send relevant notifications to interested parties,
   * for example if a cargo has been misdirected, or unloaded
   * at the final destination.
   *
   * @param trackingId cargo tracking id
   */
  void notify(TrackingId trackingId);
}
