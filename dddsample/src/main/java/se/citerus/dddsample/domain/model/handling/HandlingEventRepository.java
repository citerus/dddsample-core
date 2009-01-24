package se.citerus.dddsample.domain.model.handling;

import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.util.List;

/**
 * Handling event repository.
 */
public interface HandlingEventRepository {

  /**
   * Stores a (new) handling event.
   *
   * @param event handling event to save
   */
  void store(HandlingEvent event);


  /**
   * @param trackingId cargo tracking id
   * @return All handling events for this cargo
   */
  List<HandlingEvent> findEventsForCargo(TrackingId trackingId);

}
