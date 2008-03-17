package se.citerus.dddsample.repository;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.TrackingId;

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
    /*  There's no OR-mapped relation between the cargo delivery history and its handling events
        because the handling events are in a different aggregate.

        If this extra database call were a problem, you might want to use a different model.
        For example, you could calculate the effect/status of the cargo and store it separate from the
        handling events. */
    cargo.deliveryHistory().addAllEvents(handlingEventRepository.findEventsForCargo(tid));
    return cargo;
  }

  public void save(Cargo cargo) {
    getSession().saveOrUpdate(cargo);
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }
}
