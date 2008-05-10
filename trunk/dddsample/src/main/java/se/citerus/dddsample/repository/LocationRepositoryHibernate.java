package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.UnLocode;

import java.util.List;

public class LocationRepositoryHibernate extends HibernateRepository implements LocationRepository {

  public Location find(UnLocode unLocode) {
    return (Location) getSession().
          createQuery("from Location where unLocode = ?").
          setParameter(0, unLocode).
          uniqueResult();
  }

  public List<Location> findAll() {
    return getSession().createQuery("from Location").list();
  }

}
