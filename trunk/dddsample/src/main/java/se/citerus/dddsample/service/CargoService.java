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
   * Sends a notification to whom it may concern if a cargo is misrouted.  
   *
   * @param trackingId cargo tracking id
   */
  void notifyIfMisdirected(TrackingId trackingId);
}
