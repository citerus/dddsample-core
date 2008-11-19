package se.citerus.dddsample.application.impl;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.application.SystemEvents;
import se.citerus.dddsample.application.TrackingService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

public class TrackingServiceImpl implements TrackingService {

  private final SystemEvents systemEvents;
  private final CargoRepository cargoRepository;
  private final Log logger = LogFactory.getLog(getClass());

  public TrackingServiceImpl(SystemEvents systemEvents, CargoRepository cargoRepository) {
    this.systemEvents = systemEvents;
    this.cargoRepository = cargoRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public void onCargoHandled(final TrackingId trackingId) {
    Validate.notNull(trackingId, "Tracking ID is required");

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      logger.warn("Can't inspect non-existing cargo " + trackingId);
      return;
    }

    // TODO cargo delivery status update would happen here

    if (cargo.isMisdirected()) {
      systemEvents.cargoWasMisdirected(cargo);
    }

    if (cargo.isUnloadedAtDestination()) {
      systemEvents.cargoHasArrived(cargo);
    }
  }

}
