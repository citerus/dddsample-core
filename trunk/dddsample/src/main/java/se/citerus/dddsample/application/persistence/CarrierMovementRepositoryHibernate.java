package se.citerus.dddsample.application.persistence;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;

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
