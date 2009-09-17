package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.voyage.Voyage;

import java.util.List;

/**
 * Hibernate implementation of CargoRepository.
 */
@Repository
public class CargoRepositoryHibernate extends HibernateRepository implements CargoRepository {

  @Override
  public Cargo find(TrackingId tid) {
    return (Cargo) getSession().
      createQuery("from Cargo where trackingId = :tid").
      setParameter("tid", tid).
      uniqueResult();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Cargo> findCargosOnVoyage(Voyage voyage) {
    return getSession().createQuery(
      "select cargo from Cargo as cargo " +
        "left join cargo.itinerary.legs as leg " +
        "where leg.voyage = :voyage").
      setParameter("voyage", voyage).
      list();
  }

  @Override
  public void store(Cargo cargo) {
    getSession().saveOrUpdate(cargo);
    // Delete-orphan does not seem to work correctly when the parent is a component
    getSession().createSQLQuery("delete from Leg where cargo_id = null").executeUpdate();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Cargo> findAll() {
    return getSession().createQuery("from Cargo").list();
  }

}
