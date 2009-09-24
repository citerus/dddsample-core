package se.citerus.dddsample.tracking.core.domain.model.handling;

import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.handling.EventSequenceNumber;

/**
 * Handling event repository.
 */
public interface HandlingEventRepository {

  /**
   * @param eventSequenceNumber event sequence number
   * @return The handling event with this sequence number, or null if not found
   */
  HandlingEvent find(EventSequenceNumber eventSequenceNumber);

  /**
   * Stores a (new) handling event.
   *
   * @param event handling event to save
   */
  void store(HandlingEvent event);

  /**
   * @param cargo cargo
   * @return The handling history of this cargo
   */
  HandlingHistory lookupHandlingHistoryOfCargo(Cargo cargo);

  /**
   *
   * @param cargo cargo
   * @return The most recent handling of the cargo, or null if it has never been handled.
   */
  HandlingEvent mostRecentHandling(Cargo cargo);
}
