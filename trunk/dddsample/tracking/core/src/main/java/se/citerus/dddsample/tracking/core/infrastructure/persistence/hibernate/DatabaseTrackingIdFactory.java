/**
 * Purpose
 * @author peter
 * @created 2009-sep-17
 * $Id$
 */
package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingIdFactory;

@Repository
public class DatabaseTrackingIdFactory implements TrackingIdFactory {

  private final SessionFactory sessionFactory;
  private static final String SEQUENCE_NAME = "TRACKING_ID_SEQ";

  @Autowired
  public DatabaseTrackingIdFactory(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public TrackingId nextTrackingId() {
    final Long seq = (Long) sessionFactory.getCurrentSession().
      createSQLQuery("select next_value from system_sequences where sequence_name = ?").
      setParameter(1, SEQUENCE_NAME).
      uniqueResult();

    return new TrackingId(seq);
  }

}
