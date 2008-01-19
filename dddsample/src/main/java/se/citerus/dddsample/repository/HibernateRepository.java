package se.citerus.dddsample.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Functionality common to all Hibernate repositories.
 *
 */
public abstract class HibernateRepository {

  SessionFactory sessionFactory;

  @Required
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  protected Session getSession() {
    return sessionFactory.getCurrentSession();
  }

}
