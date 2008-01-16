package se.citerus.dddsample.repository;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.CarrierMovementId;

/**
 * Hibernate implementation of CarrierMovementRepository.
 *
 */
@Repository
public class CarrierMovementRepositoryHibernate extends HibernateRepository implements CarrierMovementRepository {

  public CarrierMovement find(CarrierMovementId carrierMovementId) {
    return (CarrierMovement) getSession().get(CarrierMovement.class, carrierMovementId);
  }

}
