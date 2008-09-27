package se.citerus.dddsample.application.persistence;

import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.carrier.SampleCarrierMovements;

public final class CarrierMovementRepositoryInMem implements CarrierMovementRepository {

  public CarrierMovement find(final CarrierMovementId carrierMovementId) {
    return SampleCarrierMovements.lookup(carrierMovementId);
  }

}
