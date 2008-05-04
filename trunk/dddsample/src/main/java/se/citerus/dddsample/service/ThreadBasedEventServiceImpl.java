package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.HandlingEvent;

/**
 * Thread based implementation.
 */
public class ThreadBasedEventServiceImpl implements EventService {
  private CargoService cargoService;

  public void fireHandlingEventRegistered(final HandlingEvent event) {
    new Thread(new Runnable() {
      public void run() {
        cargoService.notify(event.cargo().trackingId());
      }
    }).start();
  }

  public void setCargoService(CargoService cargoService) {
    this.cargoService = cargoService;
  }
}
