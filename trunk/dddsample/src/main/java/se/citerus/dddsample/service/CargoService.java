package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.domain.UnLocode;
import se.citerus.dddsample.service.dto.CargoRoutingDTO;
import se.citerus.dddsample.service.dto.CargoTrackingDTO;
import se.citerus.dddsample.service.dto.LegDTO;

import java.util.List;

/**
 * Cargo service.
 *
 */
public interface CargoService {

  /**
   * Registers a new cargo in the tracking system, not yet routed.
   *
   * @param origin cargo origin
   * @param destination cargo destination
   * @return Cargo tracking id
   */
  TrackingId registerNew(UnLocode origin, UnLocode destination);

  /**
   * @param trackingId tracking id
   * @return A cargo and its delivery history, or null if no cargo with given tracking id is found.
   */
  CargoTrackingDTO track(TrackingId trackingId);

  /**
   * @return A list of all locations where the company ships cargo to.
   */
  List<UnLocode> shippingLocations();

  /**
   * Send relevant notifications to interested parties,
   * for example if a cargo has been misdirected, or unloaded
   * at the final destination.
   *
   * @param trackingId cargo tracking id
   */
  void notify(TrackingId trackingId);

  /**
   * Loads a cargo for routing operations.
   *
   * @param trackingId cargo tracking id
   * @return A cargo with it's itinerary, or null if none found.
   */
  CargoRoutingDTO loadForRouting(TrackingId trackingId);

  /**
   * Loads all cargos for routing operations.
   *
   * @return All cargos with their itineraries.
   */
  List<CargoRoutingDTO> loadAllForRouting();

  /**
   * Assigns a new itinerary to a cargo,
   * based on this list of legs.
   *
   * @param trackingId cargo tracking id
   * @param legDTOs the legs of the route
   */
  void assignItinerary(TrackingId trackingId, List<LegDTO> legDTOs);

}
