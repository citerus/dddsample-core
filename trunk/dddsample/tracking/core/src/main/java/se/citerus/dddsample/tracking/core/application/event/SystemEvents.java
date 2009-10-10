package se.citerus.dddsample.tracking.core.application.event;

import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;

/**
 * This interface provides a way to let other parts
 * of the system know about events that have occurred.
 * <p/>
 * It may be implemented synchronously or asynchronously, using
 * for example JMS.
 */
public interface SystemEvents {

  /**
   * A cargo has been handled.
   *
   * @param event handling event
   */
  void notifyOfHandlingEvent(HandlingEvent event);

  /**
   * Cargo delivery has been updated.
   *
   * @param cargo cargo
   */
  void notifyOfCargoUpdate(Cargo cargo);

  // TODO
  //void notifyOfScheduleUpdate(Voyage voyage);

}
