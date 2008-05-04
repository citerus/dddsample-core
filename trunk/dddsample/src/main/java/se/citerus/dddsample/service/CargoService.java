package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;

/**
 * Cargo service.
 *
 */
public interface CargoService {

  /**
   * @param trackingId tracking id
   * @return A cargo and its delivery history, or null if no cargo with given tracking id is found.
   */
  CargoWithHistoryDTO track(TrackingId trackingId);

  /**
   * Send relevant notifications to interested parties,
   * for example if a cargo has been misdirected, or unloaded
   * at the final destination.
   *
   * @param trackingId cargo tracking id
   */
  void notify(TrackingId trackingId);
}
