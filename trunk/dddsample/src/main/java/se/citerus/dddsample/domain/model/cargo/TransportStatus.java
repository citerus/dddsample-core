package se.citerus.dddsample.domain.model.cargo;

import se.citerus.dddsample.domain.shared.ValueObject;
import se.citerus.dddsample.domain.model.shared.HandlingActivity;

/**
 * Represents the different transport statuses for a cargo.
 */
public enum TransportStatus implements ValueObject<TransportStatus> {
  NOT_RECEIVED, IN_PORT, ONBOARD_CARRIER, CLAIMED, UNKNOWN;

  @Override
  public boolean sameValueAs(final TransportStatus other) {
    return this.equals(other);
  }

  public static TransportStatus derivedFrom(HandlingActivity handlingActivity) {
    if (handlingActivity == null) {
      return NOT_RECEIVED;
    }

    switch (handlingActivity.type()) {
      case LOAD:
        return ONBOARD_CARRIER;
      case UNLOAD:
      case RECEIVE:
        return IN_PORT;
      case CLAIM:
        return CLAIMED;
      default:
        return UNKNOWN;
    }
  }

}
