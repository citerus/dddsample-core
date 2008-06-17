package se.citerus.dddsample.repository;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;

import java.util.List;

/**
 * Hibernate implementation of HandlingEventRepository.
 *
 */
@Repository
public class HandlingEventRepositoryHibernate extends HibernateRepository implements HandlingEventRepository {

  public void save(HandlingEvent event) {
    getSession().save(event);
  }

  public List<HandlingEvent> findEventsForCargo(TrackingId tid) {
    return getSession().createQuery(
            "from HandlingEvent where cargo.trackingId = :tid order by completionTime").
            setParameter("tid", tid).
            list();
  }

}
