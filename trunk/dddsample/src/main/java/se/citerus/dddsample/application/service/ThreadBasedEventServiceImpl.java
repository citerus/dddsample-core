package se.citerus.dddsample.application.service;

import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.service.TrackingService;

/**
 * Thread based implementation.
 */
public class ThreadBasedEventServiceImpl implements EventService {

  private TrackingService trackingService;

  public void fireHandlingEventRegistered(final HandlingEvent event) {
    new Thread(new Runnable() {
      public void run() {
        trackingService.notify(event.cargo().trackingId());
      }
    }).start();
  }

  public void setTrackingService(TrackingService trackingService) {
    this.trackingService = trackingService;
  }
}
