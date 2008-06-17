package se.citerus.dddsample.repository;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.DeliveryHistory;
import se.citerus.dddsample.domain.Itinerary;
import se.citerus.dddsample.domain.TrackingId;

import java.util.List;
import java.util.UUID;

/**
 * Hibernate implementation of CargoRepository.
 */
@Repository
public class CargoRepositoryHibernate extends HibernateRepository implements CargoRepository {

  HandlingEventRepository handlingEventRepository;

  public Cargo find(TrackingId tid) {
    Cargo cargo = (Cargo) getSession().
            createQuery("from Cargo where trackingId = :tid").
            setParameter("tid", tid).
            uniqueResult();
    if (cargo == null) {
      return null;
    }
    /*  There's no OR-mapped relation between the cargo delivery history and its handling events
        because the handling events are in a different aggregate.

        TODO: investigate the following scenario to motivate this construction 
        If we had to use pessimistic locking on cargo,
        it might be cumbersome to map the relation between cargo and handling event,
        since we want to be able to insert handling events regardless of locking status on cargo.


        If this extra database call were a problem, you might want to use a different model.
        For example, you could calculate the effect/status of the cargo and store it separate from the
        handling events. */

    /*
        TODO:
        the decision whether or not to include the delivery history when loading cargo
        seems to belong in the service layer, which defines use cases.
     */
    DeliveryHistory deliveryHistory = new DeliveryHistory(handlingEventRepository.findEventsForCargo(tid));
    cargo.setDeliveryHistory(deliveryHistory);

    return cargo;
  }

  public void save(Cargo cargo) {
    getSession().persist(cargo);

    // Delete orphaned itineraries
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
