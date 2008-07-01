package se.citerus.dddsample.domain.service;

import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;

/**
 * Thrown when trying to register an event with an unknown carrier movement id.
 */
public class UnknownCarrierMovementIdException extends Exception {

  private final CarrierMovementId carrierMovementId;

  public UnknownCarrierMovementIdException(final CarrierMovementId carrierMovementId) {
    this.carrierMovementId = carrierMovementId;
  }

  @Override
  public String getMessage() {
    return "No carrier movement with id " + carrierMovementId.idString() + " exists in the system";
  }
}
