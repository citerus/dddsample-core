package se.citerus.dddsample.tracking.core.domain.model.cargo;

import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObject;

/**
 * Represents the different transport statuses for a cargo.
 */
public enum TransportStatus implements ValueObject<TransportStatus> {
  NOT_RECEIVED, IN_PORT, ONBOARD_CARRIER, CLAIMED, UNKNOWN;

  @Override
  public boolean sameValueAs(final TransportStatus other) {
    return this.equals(other);
  }

  public static TransportStatus derivedFrom(final HandlingActivity handlingActivity) {
    if (handlingActivity == null) {
      return NOT_RECEIVED;
    }

    switch (handlingActivity.type()) {
      case LOAD:
        return ONBOARD_CARRIER;
      case UNLOAD:
      case RECEIVE:
      case CUSTOMS:
        return IN_PORT;
      case CLAIM:
        return CLAIMED;
      default:
        return UNKNOWN;
    }
  }

}
