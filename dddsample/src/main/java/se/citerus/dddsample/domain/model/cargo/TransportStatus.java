package se.citerus.dddsample.domain.model.cargo;

import se.citerus.dddsample.domain.shared.ValueObject;

/**
 * Represents the different transport statuses for a cargo.
 */
public enum TransportStatus implements ValueObject<TransportStatus> {
  NOT_RECEIVED, IN_PORT, ONBOARD_CARRIER, CLAIMED, UNKNOWN;

  @Override
  public boolean sameValueAs(final TransportStatus other) {
    return this.equals(other);
  }
}
