package se.citerus.dddsample.application.impl;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;

import java.util.List;

public class CargoInspectionServiceImpl implements CargoInspectionService {

  private final ApplicationEvents applicationEvents;
  private final CargoRepository cargoRepository;
  private final HandlingEventRepository handlingEventRepository;
  private final Log logger = LogFactory.getLog(getClass());

  public CargoInspectionServiceImpl(final ApplicationEvents applicationEvents,
                                    final CargoRepository cargoRepository,
                                    final HandlingEventRepository handlingEventRepository) {
    this.applicationEvents = applicationEvents;
    this.cargoRepository = cargoRepository;
    this.handlingEventRepository = handlingEventRepository;
  }

  @Override
  @Transactional
  public void inspectCargo(final TrackingId trackingId) {
    Validate.notNull(trackingId, "Tracking ID is required");

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      logger.warn("Can't inspect non-existing cargo " + trackingId);
      return;
    }

    final List<HandlingEvent> deliveryHistory = handlingEventRepository.findEventsForCargo(trackingId);

    cargo.deriveStatusFromHandling(deliveryHistory);

    if (cargo.isMisdirected()) {
      applicationEvents.cargoWasMisdirected(cargo);
    }

    if (cargo.isUnloadedAtDestination()) {
      applicationEvents.cargoHasArrived(cargo);
    }

    cargoRepository.store(cargo);
  }

}
