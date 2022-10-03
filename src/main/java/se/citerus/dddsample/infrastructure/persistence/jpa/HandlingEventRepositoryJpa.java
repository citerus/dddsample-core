package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

/**
 * Hibernate implementation of HandlingEventRepository.
 *
 */
public interface HandlingEventRepositoryJpa extends CrudRepository<HandlingEvent, Long>, HandlingEventRepository {

  default void store(final HandlingEvent event) {
    save(event);
  }

  HandlingHistory lookupHandlingHistoryOfCargo(final TrackingId trackingId);

}
