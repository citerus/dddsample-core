package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Functionality common to all Hibernate repositories.
 */
public abstract class HibernateRepository {

  private SessionFactory sessionFactory;

  @Required
  public void setSessionFactory(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  protected Session getSession() {
    return sessionFactory.getCurrentSession();
  }

}
