package se.citerus.dddsample.domain.model.cargo;

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
   * TODO
   * this might be too complex a procedure to belong in the repository -
   * introduce a TrackingIdFactory (or perhaps a CargoFactory).
   *
   * @return A new generated tracking Id.
   */
  TrackingId nextTrackingId();

}
