package se.citerus.dddsample.application;

import se.citerus.dddsample.application.messaging.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;


/**
 * Handling event service.
 */
public interface HandlingEventService {

  /**
   * Registers a handling event in the system, and notifies interested
   * parties that an event has been registered.
   *
   * @param attempt handling event registration attempt
   * @throws se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException
   */
  void register(HandlingEventRegistrationAttempt attempt) throws CannotCreateHandlingEventException;

}
