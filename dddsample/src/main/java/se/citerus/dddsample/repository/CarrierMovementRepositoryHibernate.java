package se.citerus.dddsample.repository;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.CarrierMovementId;

/**
 * Hibernate implementation of CarrierMovementRepository.
 */
@Repository
public final class CarrierMovementRepositoryHibernate extends HibernateRepository implements CarrierMovementRepository {

  public CarrierMovement find(final CarrierMovementId carrierMovementId) {
    return (CarrierMovement) getSession().
      createQuery("from CarrierMovement where carrierMovementId = ?").
      setParameter(0, carrierMovementId).
      uniqueResult();
  }

}
