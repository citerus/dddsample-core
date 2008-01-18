package se.citerus.dddsample.repository;

import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.HandlingEvent;

/**
 * Hibernate implementation of HandlingEventRepository.
 *
 */
@Repository
public class HandlingEventRepositoryHibernate extends HibernateRepository implements HandlingEventRepository {

  public void save(HandlingEvent event) {
    getSession().save(event);
  }

}
