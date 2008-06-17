package se.citerus.dddsample.domain.repository;

import se.citerus.dddsample.domain.model.Cargo;
import se.citerus.dddsample.domain.model.TrackingId;

import java.util.List;

public interface CargoRepository {

  /**
   * Finds a cargo using given id.
   *
   * @param trackingId Id
   * @return Cargo
   */
  Cargo find(TrackingId trackingId);

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
  void save(Cargo cargo);

  /**
   * @return A new generated tracking Id.
   */
  TrackingId nextTrackingId();

}
