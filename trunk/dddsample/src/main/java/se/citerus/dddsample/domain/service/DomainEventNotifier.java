package se.citerus.dddsample.domain.service;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;

/**
 * This interface provides a way to let other parts
 * of the system know about domain events that have occurred.
 * <p/>
 * All method signatures are expressed in the ubiquitous language.    
 * <p/>
 * It may be implemented synchronously or asynchronously, using
 * for example JMS.
 */
public interface DomainEventNotifier {

  /**
   * A cargo has been handled.
   *
   * @param event handling event
   */
  void cargoWasHandled(HandlingEvent event);

  void cargoWasMisdirected(Cargo cargo);

  void cargoHasArrived(Cargo cargo);

  //void scheduleWasChanged(Voyage voyage);

}
