package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.CarrierMovementId;

public interface CarrierMovementRepository {

  /**
   * Finds a carrier movement using given id.
   *
   * @param carrierMovementId Id
   * @return The carrier movement.
   */
  CarrierMovement find(CarrierMovementId carrierMovementId);

}
