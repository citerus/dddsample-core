package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.HandlingEvent;

/**
 * Event service.
 */
public interface EventService {

  /**
   * @param event handling event
   */
  void fireHandlingEventRegistered(HandlingEvent event);
}
