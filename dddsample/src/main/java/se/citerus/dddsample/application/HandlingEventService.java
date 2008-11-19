package se.citerus.dddsample.application;

import se.citerus.dddsample.application.messaging.HandlingEventRegistrationAttempt;


/**
 * Handling event service.
 */
public interface HandlingEventService {

  /**
   * Registers a handling event in the system, and notifies interested
   * parties that an event has been registered.
   *
   * @param attempt handling event registration attempt
   */
  void register(HandlingEventRegistrationAttempt attempt);

}
