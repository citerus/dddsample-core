package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

import java.util.List;

/**
 * Hibernate implementation of HandlingEventRepository.
 */
@Repository
public class HandlingEventRepositoryHibernate extends HibernateRepository implements HandlingEventRepository {

  @Override
  public void store(final HandlingEvent event) {
    getSession().save(event);
  }

  @Override
  public HandlingHistory lookupHandlingHistoryOfCargo(final Cargo cargo) {
    final List handlingEvents = getSession().createQuery(
      "from HandlingEvent where cargo.trackingId = :tid").
      setParameter("tid", cargo.trackingId()).
      list();

    if (handlingEvents.isEmpty()) {
      return HandlingHistory.emptyForCargo(cargo);
    } else {
      return HandlingHistory.fromEvents(handlingEvents);
    }
  }

}
