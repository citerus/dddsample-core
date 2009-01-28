package se.citerus.dddsample.application;

/**
 * Handling event service.
 */
public interface HandlingEventService {

  /**
   * Registers a handling event in the system, and notifies interested
   * parties that a cargo has been handled.
   *
   * @param attempt handling event registration attempt
   */
  void registerHandlingEvent(HandlingEventRegistrationAttempt attempt);

}
