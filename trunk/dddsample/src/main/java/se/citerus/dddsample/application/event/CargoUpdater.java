package se.citerus.dddsample.application.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.shared.EventSequenceNumber;
import se.citerus.dddsample.domain.model.shared.HandlingActivity;

public class CargoUpdater {

  private SystemEvents systemEvents;
  private CargoRepository cargoRepository;
  private HandlingEventRepository handlingEventRepository;
  private final Log logger = LogFactory.getLog(getClass());

  public CargoUpdater(final SystemEvents systemEvents,
                      final CargoRepository cargoRepository,
                      final HandlingEventRepository handlingEventRepository) {
    this.systemEvents = systemEvents;
    this.cargoRepository = cargoRepository;
    this.handlingEventRepository = handlingEventRepository;
  }

  @Transactional
  public void updateCargo(final EventSequenceNumber eventSequenceNumber) {
    final HandlingEvent handlingEvent = handlingEventRepository.find(eventSequenceNumber);
    final HandlingActivity activity = handlingEvent.activity();
    final Cargo cargo = handlingEvent.cargo();

    cargo.handled(activity);

    // TODO create domain events and deal with them as a result of the handling
    /*
    Here's an idea:

    ResultOfHandling result = cargo.effectOf(activity);
    cargo.apply(result);

    or

    cargo.handled(activity);
    Delivery delivery = cargo.currentDelivery();
    send all delivery.events();

    */

    cargoRepository.store(cargo);
    systemEvents.notifyOfCargoUpdate(cargo);
    logger.info("Updated cargo " + cargo);
  }

  CargoUpdater() {
  }

}
