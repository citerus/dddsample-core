package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.infrastructure.persistence.jpa.converters.HandlingEventDTOConverter;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.HandlingEventDTO;

/**
 * Hibernate implementation of HandlingEventRepository.
 *
 */
public interface HandlingEventRepositoryJPA extends CrudRepository<HandlingEventDTO, Long>, HandlingEventRepository {

  default void store(final HandlingEvent event) {
    save(HandlingEventDTOConverter.toDto(event));
  }

  default HandlingHistory lookupHandlingHistoryOfCargo(final TrackingId trackingId) {
    return null; // TODO implement this
  }

}
