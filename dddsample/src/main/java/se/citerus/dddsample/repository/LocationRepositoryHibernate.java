package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Location;

public class LocationRepositoryHibernate extends HibernateRepository implements LocationRepository {

  public Location find(String unlcode) {
    return (Location) getSession().
          createQuery("from Location where unlocode = ?").
          setParameter(0, unlcode).
          uniqueResult();
  }
}
