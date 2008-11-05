package se.citerus.dddsample.domain.service;

import se.citerus.dddsample.domain.model.cargo.TrackingId;

/**
 * Thrown when trying to register an event with an unknown tracking id.
 */
public final class UnknownCargoException extends Exception {

  private final TrackingId trackingId;

  public UnknownCargoException(final TrackingId trackingId) {
    this.trackingId = trackingId;
  }

  @Override
  public String getMessage() {
    return "No cargo with tracking id " + trackingId.idString() + " exists in the system";
  }
}
