package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

/**
 * Hibernate implementation of CarrierMovementRepository.
 */
@Repository
public final class VoyageRepositoryHibernate implements VoyageRepository {

  private final SessionFactory sessionFactory;

  @Autowired
  public VoyageRepositoryHibernate(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Voyage find(final VoyageNumber voyageNumber) {
    return (Voyage) sessionFactory.getCurrentSession().
      createQuery("from Voyage where voyageNumber = :vn").
      setParameter("vn", voyageNumber).
      uniqueResult();
  }

}
