package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierId;
import se.citerus.dddsample.domain.CarrierMovement;

public interface CarrierMovementRepository {

  CarrierMovement find(CarrierId carrierId);

}
