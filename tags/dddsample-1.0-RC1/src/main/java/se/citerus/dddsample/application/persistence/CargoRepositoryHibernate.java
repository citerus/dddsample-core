package se.citerus.dddsample.application.persistence;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.util.List;
import java.util.UUID;

/**
 * Hibernate implementation of CargoRepository.
 */
@Repository
public class CargoRepositoryHibernate extends HibernateRepository implements CargoRepository {

  public Cargo find(TrackingId tid) {
    // Query for id and then perform a standard load().
    // This way we use the metadata-defined way of loading the aggregate
    // in an efficient way (generally a complete aggregate at a time),
    // and we can benefi from the identifier-keyed second level cache
    // without havng to cache individual queries.
    Long id = (Long) getSession().
       createQuery("select id from Cargo where trackingId = :tid").
       setParameter("tid", tid).
       uniqueResult();

    if (id != null) {
      return (Cargo) getSession().get(Cargo.class, id);
    } else {
      return null;
    }


  }

  public void save(Cargo cargo) {
    getSession().persist(cargo);

    // Delete orphaned itineraries - conceptually the responsibility
    // of the Cargo aggregate
    final List<Itinerary> orphans = getSession().
       createQuery("from Itinerary where cargo = null").
       list();
    for (Itinerary orphan : orphans) {
      getSession().delete(orphan);
    }
  }

  public TrackingId nextTrackingId() {
    final String random = UUID.randomUUID().toString().toUpperCase();
    return new TrackingId(
       random.substring(0, random.indexOf("-"))
    );
  }

  public List<Cargo> findAll() {
    return getSession().createQuery("from Cargo").list();
  }

}
