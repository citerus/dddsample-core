package se.citerus.dddsample.application.service;

import se.citerus.dddsample.domain.model.HandlingEvent;

/**
 * Event service.
 */
public interface EventService {

  /**
   * @param event handling event
   */
  void fireHandlingEventRegistered(HandlingEvent event);
}
