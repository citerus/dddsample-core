package se.citerus.dddsample.domain.repository;

import se.citerus.dddsample.domain.model.CarrierMovement;
import se.citerus.dddsample.domain.model.CarrierMovementId;

public interface CarrierMovementRepository {

  /**
   * Finds a carrier movement using given id.
   *
   * @param carrierMovementId Id
   * @return The carrier movement.
   */
  CarrierMovement find(CarrierMovementId carrierMovementId);

}
