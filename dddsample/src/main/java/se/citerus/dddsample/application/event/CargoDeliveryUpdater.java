package se.citerus.dddsample.application.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;

public class CargoDeliveryUpdater {

  private ApplicationEvents applicationEvents;
  private CargoRepository cargoRepository;
  private HandlingEventRepository handlingEventRepository;
  private final Log logger = LogFactory.getLog(getClass());

  public CargoDeliveryUpdater(final ApplicationEvents applicationEvents,
                              final CargoRepository cargoRepository,
                              final HandlingEventRepository handlingEventRepository) {
    this.applicationEvents = applicationEvents;
    this.cargoRepository = cargoRepository;
    this.handlingEventRepository = handlingEventRepository;
  }

  @Transactional
  public void updateDelivery(final TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);
    final HandlingEvent handlingEvent = handlingEventRepository.mostRecentHandling(cargo);

    // TODO still doesn't sound right...cargo.updateDelivery()?
    cargo.handled(handlingEvent.handlingActivity());

    cargoRepository.store(cargo);
    applicationEvents.cargoDeliveryWasUpdated(cargo);
    logger.info("Updated delivery of cargo " + cargo + ": " + cargo.delivery());
  }

  CargoDeliveryUpdater() {
  }

}
