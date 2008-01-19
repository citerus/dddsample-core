package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.TrackingId;

/**
 * Thrown when trying to register an event with an unknown tracking id.
 *
 */
public class UnknownTrackingIdException extends Exception {
  private TrackingId trackingId;

  public UnknownTrackingIdException(TrackingId trackingId) {
    this.trackingId = trackingId;
  }

  @Override
  public String getMessage() {
    return "No cargo with tracking id " + trackingId.idString() + " exists in the system";
  }
}
