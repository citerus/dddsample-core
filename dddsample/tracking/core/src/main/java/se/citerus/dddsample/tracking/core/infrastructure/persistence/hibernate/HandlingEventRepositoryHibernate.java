package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.tracking.core.domain.model.handling.EventSequenceNumber;

import java.util.List;

/**
 * Hibernate implementation of HandlingEventRepository.
 */
@Repository
public class HandlingEventRepositoryHibernate implements HandlingEventRepository {

  private final SessionFactory sessionFactory;

  @Autowired
  public HandlingEventRepositoryHibernate(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public HandlingEvent find(final EventSequenceNumber eventSequenceNumber) {
    return (HandlingEvent) sessionFactory.getCurrentSession().
      createQuery("from HandlingEvent where sequenceNumber = :sn").
      setParameter("sn", eventSequenceNumber).
      uniqueResult();
  }

  @Override
  public void store(final HandlingEvent event) {
    sessionFactory.getCurrentSession().save(event);
  }

  @Override
  public HandlingHistory lookupHandlingHistoryOfCargo(final Cargo cargo) {
    final List handlingEvents = sessionFactory.getCurrentSession().
      createQuery("from HandlingEvent where cargo.trackingId = :tid").
      setParameter("tid", cargo.trackingId()).
      list();

    if (handlingEvents.isEmpty()) {
      return HandlingHistory.emptyForCargo(cargo);
    } else {
      //noinspection unchecked
      return HandlingHistory.fromEvents(handlingEvents);
    }
  }

  @Override
  public HandlingEvent mostRecentHandling(final Cargo cargo) {
    return (HandlingEvent) sessionFactory.getCurrentSession().
        createQuery("from HandlingEvent where cargo = :cargo order by completionTime desc").
        setParameter("cargo", cargo).
        setMaxResults(1).
        uniqueResult();
  }

}
