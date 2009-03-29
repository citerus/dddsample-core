package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;
import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;

import java.lang.reflect.Field;

public abstract class AbstractRepositoryTest extends AbstractTransactionalDataSourceSpringContextTests {

  SessionFactory sessionFactory;
  SimpleJdbcTemplate sjt;
  HandlingEventFactory handlingEventFactory;
  HandlingEventRepository handlingEventRepository;

  public void setHandlingEventFactory(HandlingEventFactory handlingEventFactory) {
    this.handlingEventFactory = handlingEventFactory;
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }

  protected AbstractRepositoryTest() {
    setAutowireMode(AUTOWIRE_BY_NAME);
    setDependencyCheck(false);
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
    transactionManager = new HibernateTransactionManager(sessionFactory);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  protected void flush() {
    sessionFactory.getCurrentSession().flush();
  }

  @Override
  protected String[] getConfigLocations() {
    return new String[] {"/context-infrastructure-persistence.xml", "context-domain.xml"};
  }

  @Override
  protected void onSetUpInTransaction() throws Exception {
    // TODO store Sample* and object instances here instead of handwritten SQL
    SampleDataGenerator.loadSampleData(jdbcTemplate, new TransactionTemplate(transactionManager));
    sjt = new SimpleJdbcTemplate(jdbcTemplate);
  }

  protected Session getSession() {
    return sessionFactory.getCurrentSession();
  }

  // Instead of exposing a getId() on persistent classes
  protected Long getLongId(Object o) {
    if (getSession().contains(o)) {
      return (Long) getSession().getIdentifier(o);
    } else {
      try {
        Field id = o.getClass().getDeclaredField("id");
        id.setAccessible(true);
        return (Long) id.get(o);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
