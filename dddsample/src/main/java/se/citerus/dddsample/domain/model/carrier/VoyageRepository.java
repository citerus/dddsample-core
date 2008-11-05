package se.citerus.dddsample.domain.model.carrier;

public interface VoyageRepository {

  /**
   * Finds a voyage using voyage number.
   *
   * @param voyageNumber voyage number
   * @return The voyage, or null if not found.
   */
  Voyage find(VoyageNumber voyageNumber);

}
