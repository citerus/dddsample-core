package se.citerus.dddsample.service;

import java.util.Date;


/**
 * Handling event service.
 *
 */
public interface HandlingEventService {

  /**
   * Register that a cargo has been loaded.
   *
   * @param completionTime when the event was completed, for example finished loading
   * @param carrierMovementId carrier movement id
   * @param trackingIds tracking ids of cargos to register this event for
   */
  void registerLoad(Date completionTime, String carrierMovementId, String[] trackingIds);

  /**
   * Register that a cargo has been unloaded.
   *
   * @param completionTime when the event was completed, for example finished loading
   * @param carrierMovementId carrier movement id
   * @param trackingIds tracking ids of cargos to register this event for
   */
  void registerUnload(Date completionTime, String carrierMovementId, String[] trackingIds);

  /**
   * Register that a cargo has been claimed.
   *
   * @param completionTime when the event was completed, for example finished loading
   * @param unlocode United Nations Location Code, for example "SESTO" for SwEden/STOckholm
   * @param trackingIds tracking ids of cargos to register this event for
   */
  void registerClaim(Date completionTime, String unlocode, String[] trackingIds);

  /**
   * Register that a cargo has been recieved.
   *
   * @param completionTime when the event was completed, for example finished loading
   * @param unlocode United Nations Location Code, for example "SESTO" for SwEden/STOckholm
   * @param trackingIds tracking ids of cargos to register this event for
   */
  void registerRecieve(Date completionTime, String unlocode, String[] trackingIds);

  /**
   * Register that a cargo has been cleared by customs.
   *
   * @param completionTime when the event was completed, for example finished loading
   * @param unlocode United Nations Location Code, for example "SESTO" for SwEden/STOckholm
   * @param trackingIds tracking ids of cargos to register this event for
   */
  void registerCustomsCleared(Date completionTime, String unlocode, String[] trackingIds);
}