package se.citerus.dddsample.service;

import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;

/**
 * Cargo service.
 *
 */
public interface CargoService {

  /**
   * @param trackingId tracking id
   * @return A cargo and its delivery history, or null if no cargo with given tracking id is found.
   */
  CargoWithHistoryDTO track(String trackingId);

}
