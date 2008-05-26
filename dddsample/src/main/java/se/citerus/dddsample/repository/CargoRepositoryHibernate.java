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
    DeliveryHistory deliveryHistory = new DeliveryHistory(handlingEventRepository.findEventsForCargo(tid));
    cargo.setDeliveryHistory(deliveryHistory);

    return cargo;
  }

  public void save(Cargo cargo) {
    getSession().saveOrUpdate(cargo);
  }

  public TrackingId nextTrackingId() {
    // TODO:
    // Could be an opportunity to maybe illustrate how to handle pessimistic locking
    // and aggregate boundaries, and maybe problems with a distributed application
    // sharing a database. For now it's simply random though.
    String random = UUID.randomUUID().toString().toUpperCase();
    return new TrackingId(
      random.substring(0, random.indexOf("-"))
    );
  }

  public void deleteItinerary(Itinerary itinerary) {
    // Itinerary should be mapped to cascade deletes to all its legs
    if (itinerary != null) {
      getSession().delete(itinerary);
    }
  }

  public List<Cargo> findAll() {
    return getSession().createQuery("from Cargo").list();
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }
}
