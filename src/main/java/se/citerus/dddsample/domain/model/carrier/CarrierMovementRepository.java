package se.citerus.dddsample.domain.model.carrier;

public interface CarrierMovementRepository {

  /**
   * Finds a carrier movement using given id.
   *
   * @param carrierMovementId Id
   * @return The carrier movement.
   */
  CarrierMovement find(CarrierMovementId carrierMovementId);

}
