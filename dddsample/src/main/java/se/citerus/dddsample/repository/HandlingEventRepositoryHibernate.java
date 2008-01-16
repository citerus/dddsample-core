package se.citerus.dddsample.repository;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.DeliveryHistory;
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

  public DeliveryHistory findDeliveryHistory(TrackingId trackingId) {
    List list = getSession().createQuery(
            "from HandlingEvent he where he.cargo.trackingId = :tid").
            setParameter("tid", trackingId).
            list();
    return new DeliveryHistory(list);
  }

}
