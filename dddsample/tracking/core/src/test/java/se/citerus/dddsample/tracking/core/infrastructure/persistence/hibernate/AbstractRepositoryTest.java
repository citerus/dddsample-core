package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;
import se.citerus.dddsample.tracking.core.application.util.SampleDataGenerator;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;

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

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  protected void flush() {
    sessionFactory.getCurrentSession().flush();
  }

  @Override
  protected String[] getConfigLocations() {
    return new String[] {
      "contexts/context-infrastructure-persistence.xml",
      "contexts/context-domain.xml"
    };
  }

  @Override
  protected void onSetUpInTransaction() throws Exception {
    // TODO store Sample* and object instances here instead of handwritten SQL
    //SampleDataGenerator.loadSampleData(jdbcTemplate, new TransactionTemplate(transactionManager));
    SampleDataGenerator.loadHibernateData(new TransactionTemplate(transactionManager), sessionFactory, handlingEventFactory, handlingEventRepository);
    sjt = new SimpleJdbcTemplate(jdbcTemplate);
  }

  protected Session getSession() {
    return sessionFactory.getCurrentSession();
  }

}
