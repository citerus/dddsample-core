package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Itinerary;
import se.citerus.dddsample.domain.TrackingId;

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

  /**
   * Deletes an itinerary.
   *
   * @param itinerary itinerary to delete
   */
  void deleteItinerary(Itinerary itinerary);
}
