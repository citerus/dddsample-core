package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierMovement;

public interface CarrierRepository {

  CarrierMovement find(String carrierId);

}
