package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.CarrierMovementId;

public interface CarrierMovementRepository {

  CarrierMovement find(CarrierMovementId carrierMovementId);

}
