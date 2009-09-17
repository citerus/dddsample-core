package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.shared.EventSequenceNumber;

import java.util.List;

/**
 * Hibernate implementation of HandlingEventRepository.
 */
@Repository
public class HandlingEventRepositoryHibernate extends HibernateRepository implements HandlingEventRepository {

  @Override
  public HandlingEvent find(EventSequenceNumber eventSequenceNumber) {
    return (HandlingEvent) getSession().
      createQuery("from HandlingEvent where sequenceNumber = :sn").
      setParameter("sn", eventSequenceNumber).
      uniqueResult();
  }

  @Override
  public void store(final HandlingEvent event) {
    getSession().save(event);
  }

  @Override
  public HandlingHistory lookupHandlingHistoryOfCargo(final Cargo cargo) {
    final List handlingEvents = getSession().
      createQuery("from HandlingEvent where cargo.trackingId = :tid").
      setParameter("tid", cargo.trackingId()).
      list();

    if (handlingEvents.isEmpty()) {
      return HandlingHistory.emptyForCargo(cargo);
    } else {
      return HandlingHistory.fromEvents(handlingEvents);
    }
  }

  @Override
  public HandlingEvent mostRecentHandling(Cargo cargo) {
      return (HandlingEvent) getSession().
        createQuery("from HandlingEvent where cargo = :cargo order by completionTime desc").
        setParameter("cargo", cargo).
        setMaxResults(1).
        uniqueResult();
  }

}
