package se.citerus.dddsample.domain.model.cargo;

/**
 * Represents the different status codes for a cargo.
 */
public enum StatusCode {
  // TODO status code for "not routed"?
  NOT_RECEIVED, IN_PORT, ONBOARD_CARRIER, CLAIMED, UNKNOWN
}
