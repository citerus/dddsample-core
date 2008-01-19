package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.HandlingEvent;

import java.util.Arrays;

/**
 * Thrown when trying to register an event with an invalid type.
 *
 */
public class InvalidEventTypeException extends Throwable {
  private String eventType;

  public InvalidEventTypeException(String eventType) {
    this.eventType = eventType;
  }

  public String getMessage() {
    return "Invalid event type: " + eventType + ". Valid types are: " + Arrays.deepToString(HandlingEvent.Type.values());
  }
}
