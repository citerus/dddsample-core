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

  public Cargo find(TrackingId trackingId) {
    Cargo cargo = (Cargo) getSession().
            createQuery("from Cargo where trackingId = ?").
            setParameter(0, trackingId).
            uniqueResult();
    /*
        If this extra database call were a problem, you might want to use a different model.
        For example, calculate the effect/status of the cargo and store it separate from the
        handling events.
     */
    cargo.deliveryHistory().addAllEvents(handlingEventRepository.findEventsForCargo(trackingId));
    return cargo;
  }

  public void save(Cargo cargo) {
    getSession().saveOrUpdate(cargo);
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }
}
