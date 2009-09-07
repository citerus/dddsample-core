package se.citerus.dddsample.domain.model.handling;

import se.citerus.dddsample.domain.model.cargo.Cargo;

/**
 * Handling event repository.
 */
public interface HandlingEventRepository {

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
