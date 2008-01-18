package se.citerus.dddsample.repository;

import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;
import se.citerus.dddsample.util.SampleDataGenerator;

public abstract class AbstractRepositoryTest extends AbstractTransactionalDataSourceSpringContextTests {

  SessionFactory sessionFactory;
  SimpleJdbcTemplate sjt;

  protected AbstractRepositoryTest() {
    setDependencyCheck(false);
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
    transactionManager = new HibernateTransactionManager(sessionFactory);
  }

  protected void flush() {
    sessionFactory.getCurrentSession().flush();
  }

  @Override
  protected String[] getConfigLocations() {
    return new String[] {"context-persistence.xml"};
  }

  @Override
  protected void onSetUpInTransaction() throws Exception {
    SampleDataGenerator.loadSampleData(jdbcTemplate, new TransactionTemplate(transactionManager));
    sjt = new SimpleJdbcTemplate(jdbcTemplate);
  }

}
