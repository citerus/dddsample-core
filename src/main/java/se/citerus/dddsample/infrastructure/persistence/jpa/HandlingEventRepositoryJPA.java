package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.infrastructure.persistence.jpa.converters.HandlingEventDTOConverter;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.HandlingEventDTO;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Hibernate implementation of HandlingEventRepository.
 *
 */
public interface HandlingEventRepositoryJPA extends CrudRepository<HandlingEventDTO, Long>, HandlingEventRepository {

  default void store(final HandlingEvent event) {
    save(HandlingEventDTOConverter.toDto(event));
  }

  default HandlingHistory lookupHandlingHistoryOfCargo(final TrackingId trackingId) {
    // TODO replace with SQL code in annotation
    List<HandlingEvent> handlingEventDtos = StreamSupport.stream(findAll().spliterator(), false)
            .filter(el -> el.cargo.trackingId.equalsIgnoreCase(trackingId.idString()))
            .map(HandlingEventDTOConverter::fromDto)
            .collect(Collectors.toList());
    return new HandlingHistory(handlingEventDtos);
  }

}
