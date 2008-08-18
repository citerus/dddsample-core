package se.citerus.dddsample.application.persistence;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;

import java.util.List;
import java.util.UUID;

/**
 * Hibernate implementation of CargoRepository.
 */
@Repository
public class CargoRepositoryHibernate extends HibernateRepository implements CargoRepository {

  HandlingEventRepository handlingEventRepository;

  public Cargo find(TrackingId tid) {
    // Query for id and then perform a standard load()
    // to use metadata-defined query and lazy proxy access
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

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }
}
