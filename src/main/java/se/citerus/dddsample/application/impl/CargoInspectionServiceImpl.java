package se.citerus.dddsample.application.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class CargoInspectionServiceImpl implements CargoInspectionService {

  private final ApplicationEvents applicationEvents;
  private final CargoRepository cargoRepository;
  private final HandlingEventRepository handlingEventRepository;
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
    Objects.requireNonNull(trackingId, "Tracking ID is required");

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      logger.warn("Can't inspect non-existing cargo {}", trackingId);
      return;
    }

    final HandlingHistory handlingHistory = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId);

    cargo.deriveDeliveryProgress(handlingHistory);

    if (cargo.delivery().isMisdirected()) {
      applicationEvents.cargoWasMisdirected(cargo);
    }

    if (cargo.delivery().isUnloadedAtDestination()) {
      applicationEvents.cargoHasArrived(cargo);
    }

    cargoRepository.store(cargo);
  }
}
