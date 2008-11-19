package se.citerus.dddsample.application;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;


/**
 * Handling event service.
 */
public interface HandlingEventService {

  /**
   * Registers a handling event in the system, and notifies interested
   * parties that an event has been registered.
   *
   * @param event handling event to register
   */
  @Transactional
  void register(HandlingEvent event);

}
