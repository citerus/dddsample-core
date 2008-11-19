package se.citerus.dddsample.application.impl;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.application.TrackingService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.service.DomainEventNotifier;

public class TrackingServiceImpl implements TrackingService {

  private final DomainEventNotifier domainEventNotifier;
  private final CargoRepository cargoRepository;
  private final Log logger = LogFactory.getLog(getClass());

  public TrackingServiceImpl(DomainEventNotifier domainEventNotifier, CargoRepository cargoRepository) {
    this.domainEventNotifier = domainEventNotifier;
    this.cargoRepository = cargoRepository;
  }

  @Override
  @Transactional
  public void onCargoHandled(final TrackingId trackingId) {
    Validate.notNull(trackingId, "Tracking ID is required");

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      logger.warn("Can't inspect non-existing cargo " + trackingId);
      return;
    }

    // TODO cargo delivery status update would happen here

    if (cargo.isMisdirected()) {
      domainEventNotifier.cargoWasMisdirected(cargo);
    }

    if (cargo.isUnloadedAtDestination()) {
      domainEventNotifier.cargoHasArrived(cargo);
    }
  }

}
