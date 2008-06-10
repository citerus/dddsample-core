package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Itinerary;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.domain.UnLocode;
import se.citerus.dddsample.service.dto.CargoTrackingDTO;

import java.util.List;

/**
 * Cargo service.
 */
public interface CargoService {

  /**
   * @param trackingId tracking id
   * @return A cargo and its delivery history, or null if no cargo with given tracking id is found.
   */
  CargoTrackingDTO track(TrackingId trackingId);

  /**
   * Send relevant notifications to interested parties,
   * for example if a cargo has been misdirected, or unloaded
   * at the final destination.
   *
   * @param trackingId cargo tracking id
   */
  void notify(TrackingId trackingId);

  /**
   * @return A list of all locations where the company ships cargo to.
   */
  List<UnLocode> listShippingLocations();

  /**
   * Lists all cargos.
   *
   * @return All cargos.
   */
  List<Cargo> listAllCargos();

  /**
   * Registers a new cargo in the tracking system, not yet routed.
   *
   * @param origin      cargo origin
   * @param destination cargo destination
   * @return Cargo tracking id
   */
  TrackingId registerNewCargo(UnLocode origin, UnLocode destination);

  /**
   * Loads a cargo for routing operations.
   *
   * @param trackingId cargo tracking id
   * @return A cargo with it's itinerary, or null if none found.
   */
  Cargo loadCargoForRouting(TrackingId trackingId);

  /**
   * Assigns a cargo to route.
   *
   * @param trackingId cargo tracking id
   * @param itinerary  the new itinerary describing the route
   */
  void assignCargoToRoute(TrackingId trackingId, Itinerary itinerary);

}
