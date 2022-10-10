package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

import java.util.List;

/**
 * Hibernate implementation of HandlingEventRepository.
 *
 */
public interface HandlingEventRepositoryJPA extends CrudRepository<HandlingEvent, Long>, HandlingEventRepository {

  default void store(final HandlingEvent event) {
    save(event);
  }

  default HandlingHistory lookupHandlingHistoryOfCargo(final TrackingId trackingId) {
    return new HandlingHistory(getHandlingHistoryOfCargo(trackingId.idString()));
  }

  @Query("select he from HandlingEvent he where he.cargo.trackingId = :trackingId and he.location != NULL")
  List<HandlingEvent> getHandlingHistoryOfCargo(String trackingId);

}
