package se.citerus.dddsample.domain.model.handling;

import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.util.List;

/**
 * Handling event repository.
 */
public interface HandlingEventRepository {

  /**
   * Saves a (new) handling event.
   *
   * @param event handling event to save
   */
  void save(final HandlingEvent event);


  // TODO: remove this in 1.0 branch, it's not used anywhere
  /**
   * @param trackingId cargo tracking id
   * @return All handling events for this cargo, ordered by completion time.
   */
  List<HandlingEvent> findEventsForCargo(final TrackingId trackingId);
}
