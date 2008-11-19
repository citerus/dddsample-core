package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;

/**
 * Hibernate implementation of CarrierMovementRepository.
 */
@Repository
public final class VoyageRepositoryHibernate extends HibernateRepository implements VoyageRepository {

  public Voyage find(final VoyageNumber voyageNumber) {
    return (Voyage) getSession().
      createQuery("from Voyage where voyageNumber = :vn").
      setParameter("vn", voyageNumber).
      uniqueResult();
  }

}
