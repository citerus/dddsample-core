package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

/**
 * Hibernate implementation of CarrierMovementRepository.
 */
@Repository
public class VoyageRepositoryHibernate extends HibernateRepository implements VoyageRepository {

  public VoyageRepositoryHibernate(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Voyage find(final VoyageNumber voyageNumber) {
    return (Voyage) getSession().
      createQuery("from Voyage where voyageNumber = :vn").
      setParameter("vn", voyageNumber).
      uniqueResult();
  }

}
