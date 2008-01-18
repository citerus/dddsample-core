package se.citerus.dddsample.repository;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.TrackingId;

/**
 * Hibernate implementation of CargoRepository.
 */
@Repository
public class CargoRepositoryHibernate extends HibernateRepository implements CargoRepository {


  public Cargo find(TrackingId trackingId) {
    return (Cargo) getSession().
            createQuery("from Cargo where trackingId = ?").
            setParameter(0, trackingId).
            uniqueResult();
  }

  public void save(Cargo cargo) {
    getSession().saveOrUpdate(cargo);
  }

}
