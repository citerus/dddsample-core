package se.citerus.dddsample.application;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;

/**
 * This interface provides a way to let other parts
 * of the system know about events that have occurred.
 * <p/>
 * It may be implemented synchronously or asynchronously, using
 * for example JMS.
 */
public interface ApplicationEvents {

  /**
   * A cargo has been handled.
   *
   * @param event handling event
   */
  void cargoWasHandled(HandlingEvent event);

  /**
   * Cargo delivery has been updated.
   * @param cargo cargo
   */
  void cargoDeliveryWasUpdated(Cargo cargo);

  // TODO remove the two below 

  /**
   * A cargo has been misdirected.
   *
   * @param cargo cargo
   */
  void cargoWasMisdirected(Cargo cargo);

  /**
   * A cargo has arrived at its final destination.
   *
   * @param cargo cargo
   */
  void cargoHasArrived(Cargo cargo);

}
