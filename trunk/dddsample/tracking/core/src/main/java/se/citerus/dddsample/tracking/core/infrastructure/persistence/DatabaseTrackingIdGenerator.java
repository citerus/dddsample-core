/**
 * Purpose
 * @author peter
 * @created 2009-sep-17
 * $Id$
 */
package se.citerus.dddsample.tracking.core.infrastructure.persistence;

import org.hibernate.SessionFactory;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingIdGenerator;

public class DatabaseTrackingIdGenerator implements TrackingIdGenerator {

  private final SessionFactory sessionFactory;
  private static final String SEQUENCE_NAME = "TRACKING_ID_SEQ";

  public DatabaseTrackingIdGenerator(final SessionFactory sessionFactory) {
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
