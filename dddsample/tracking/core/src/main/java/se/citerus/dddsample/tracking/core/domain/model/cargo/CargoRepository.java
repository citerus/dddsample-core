package se.citerus.dddsample.tracking.core.domain.model.cargo;

import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;

import java.util.List;

public interface CargoRepository {

  /**
   * Finds a cargo using given id.
   *
   * @param trackingId Id
   * @return Cargo if found, else {@code null}
   */
  Cargo find(TrackingId trackingId);

  /**
   *
   * @param voyage
   * @return
   */
  List<Cargo> findCargosOnVoyage(Voyage voyage);

  /**
   * Finds all cargo.
   *
   * @return All cargo.
   */
  List<Cargo> findAll();

  /**
   * Saves given cargo.
   *
   * @param cargo cargo to save
   */
  void store(Cargo cargo);

}
