package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.HandlingEvent;

/**
 * Handling event repository.
 *
 */
public interface HandlingEventRepository {

  /**
   * Saves a (new) handling event.
   *
   * @param event handling event to save
   */
  void save(HandlingEvent event);

}
