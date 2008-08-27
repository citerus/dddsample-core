package se.citerus.dddsample.application.messaging;

import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.service.DomainEventNotifier;
import se.citerus.dddsample.domain.service.TrackingService;

/**
 * Thread based implementation.
 */
public class ThreadBasedDomainEventNotifierImpl implements DomainEventNotifier {

  private TrackingService trackingService;

  public void cargoWasHandled(final HandlingEvent event) {
    new Thread(new Runnable() {
      public void run() {
        trackingService.inspectCargo(event.cargo().trackingId());
      }
    }).start();
  }

  public void setTrackingService(TrackingService trackingService) {
    this.trackingService = trackingService;
  }
}
