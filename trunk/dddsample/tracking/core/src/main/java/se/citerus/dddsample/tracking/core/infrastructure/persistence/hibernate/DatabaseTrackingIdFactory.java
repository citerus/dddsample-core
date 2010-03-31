/**
 * Purpose
 * @author peter
 * @created 2009-sep-17
 * $Id$
 */
package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingIdFactory;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.sql.SQLException;

@Repository
public class DatabaseTrackingIdFactory implements TrackingIdFactory {

  private final SessionFactory sessionFactory;
  private static final String SEQUENCE_NAME = "TRACKING_ID_SEQ";

  @Autowired
  public DatabaseTrackingIdFactory(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @PostConstruct
  public void createSequence() {
    final HibernateTemplate template = new HibernateTemplate(sessionFactory);
    final HibernateCallback callback = new HibernateCallback() {
      @Override
      public Object doInHibernate(final Session session) throws HibernateException, SQLException {
        return session.createSQLQuery("create sequence " + SEQUENCE_NAME + " as bigint start with 1").executeUpdate();
      }
    };

    template.execute(callback);
  }

  @Override
  public TrackingId nextTrackingId() {
    final BigInteger seq = (BigInteger) sessionFactory.getCurrentSession().
      createSQLQuery("call next value for " + SEQUENCE_NAME).
      uniqueResult();

    return new TrackingId(seq.longValue());
  }

}
