package se.citerus.dddsample.tracking.core.application.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.handling.EventSequenceNumber;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;

@Service
public class CargoUpdater {

  private SystemEvents systemEvents;
  private CargoRepository cargoRepository;
  private HandlingEventRepository handlingEventRepository;
  private final Log logger = LogFactory.getLog(getClass());

  @Autowired
  public CargoUpdater(SystemEvents systemEvents,
                      CargoRepository cargoRepository,
                      HandlingEventRepository handlingEventRepository) {
    this.systemEvents = systemEvents;
    this.cargoRepository = cargoRepository;
    this.handlingEventRepository = handlingEventRepository;
  }

  @Transactional
  public void updateCargo(final EventSequenceNumber sequenceNumber) {
    final HandlingEvent handlingEvent = handlingEventRepository.find(sequenceNumber);
    if (handlingEvent == null) {
      logger.error("Could not find any handling event with sequence number " + sequenceNumber);
      return;
    }

    final HandlingActivity activity = handlingEvent.activity().copy();
    final Cargo cargo = handlingEvent.cargo();

    cargo.handled(activity);
    cargoRepository.store(cargo);

    systemEvents.notifyOfCargoUpdate(cargo);
    logger.info("Updated cargo " + cargo);
  }

  CargoUpdater() {
    // Needed by CGLIB
  }

}
