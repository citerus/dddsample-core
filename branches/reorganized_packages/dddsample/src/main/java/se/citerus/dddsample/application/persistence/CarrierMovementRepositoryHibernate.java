package se.citerus.dddsample.application.persistence;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.CarrierMovement;
import se.citerus.dddsample.domain.model.CarrierMovementId;
import se.citerus.dddsample.domain.repository.CarrierMovementRepository;

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
