package se.citerus.dddsample.domain.model.location;

import java.util.List;

public interface LocationRepository {

  /**
   * Finds a location using given unlocode.
   *
   * @param unLocode UNLocode.
   * @return Location.
   */
  Location find(UnLocode unLocode);

  /**
   * Finds all locations.
   *
   * @return All locations.
   */
  List<Location> findAll();

}
