package se.citerus.dddsample.application.persistence;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.Location;
import se.citerus.dddsample.domain.model.UnLocode;
import se.citerus.dddsample.domain.repository.LocationRepository;

import java.util.List;

@Repository
public final class LocationRepositoryHibernate extends HibernateRepository implements LocationRepository {

  public Location find(final UnLocode unLocode) {
    return (Location) getSession().
      createQuery("from Location where unLocode = ?").
      setParameter(0, unLocode).
      uniqueResult();
  }

  public List<Location> findAll() {
    return getSession().createQuery("from Location").list();
  }

}
