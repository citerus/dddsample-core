package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;

import java.util.List;

/**
 * Hibernate implementation of CargoRepository.
 */
@Repository
public class CargoRepositoryHibernate implements CargoRepository {

  private final SessionFactory sessionFactory;

  @Autowired
  public CargoRepositoryHibernate(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Cargo find(final TrackingId tid) {
    return (Cargo) sessionFactory.getCurrentSession().
      createQuery("from Cargo where trackingId = :tid").
      setParameter("tid", tid).
      uniqueResult();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Cargo> findCargosOnVoyage(final Voyage voyage) {
    return sessionFactory.getCurrentSession().createQuery(
      "select cargo from Cargo as cargo " +
        "left join cargo.itinerary.legs as leg " +
        "where leg.voyage = :voyage").
      setParameter("voyage", voyage).
      list();
  }

  @Override
  public void store(final Cargo cargo) {
    sessionFactory.getCurrentSession().saveOrUpdate(cargo);
    // Delete-orphan does not seem to work correctly when the parent is a component
    sessionFactory.getCurrentSession().createSQLQuery("delete from Leg where cargo_id = null").executeUpdate();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Cargo> findAll() {
    return sessionFactory.getCurrentSession().createQuery("from Cargo").list();
  }

}
