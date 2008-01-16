package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.DeliveryHistory;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;

/**
 * Handling event repository.
 *
 */
public interface HandlingEventRepository {

  /**
   * Saves a (new) handling event.
   *
   * @param event handling event to save
   */
  void save(HandlingEvent event);

  /**
   * @param trackingId cargo tracking id
   * @return The delivery history of the cargo with the given tracking id.
   */
  DeliveryHistory findDeliveryHistory(TrackingId trackingId);

}
