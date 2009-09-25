package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;

import java.util.List;

@Repository
public final class LocationRepositoryHibernate implements LocationRepository {

  private final SessionFactory sessionFactory;

  @Autowired
  public LocationRepositoryHibernate(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Location find(final UnLocode unLocode) {
    return (Location) sessionFactory.getCurrentSession().
      createQuery("from Location where unLocode = ?").
      setParameter(0, unLocode).
      uniqueResult();
  }

  @SuppressWarnings("unchecked")
  public List<Location> findAll() {
    return sessionFactory.getCurrentSession().createQuery("from Location").list();
  }

}
