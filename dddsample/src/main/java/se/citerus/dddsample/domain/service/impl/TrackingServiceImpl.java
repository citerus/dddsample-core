package se.citerus.dddsample.domain.service.impl;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.service.DomainEventNotifier;
import se.citerus.dddsample.domain.service.TrackingService;

public class TrackingServiceImpl implements TrackingService {

  private final DomainEventNotifier domainEventNotifier;
  private final CargoRepository cargoRepository;
  private final Log logger = LogFactory.getLog(getClass());

  public TrackingServiceImpl(DomainEventNotifier domainEventNotifier, CargoRepository cargoRepository) {
    this.domainEventNotifier = domainEventNotifier;
    this.cargoRepository = cargoRepository;
  }

  public Cargo track(final TrackingId trackingId) {
    // TODO this does not add any value over calling repository
    // Perhaps this service should be remodeled to only handle inspection
    // and state updating 
    Validate.notNull(trackingId);

    return cargoRepository.find(trackingId);
  }

  public void inspectCargo(final TrackingId trackingId) {
    // TODO this method name is not descriptive enough
    // For example, onCargoHandling(), whenCargoIsHandled(), actOnHandling() 
    // mirrors DomainEventNotifier.cargoWasHandled()
    Validate.notNull(trackingId);

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      logger.warn("Can't inspect non-existing cargo " + trackingId);
      return;
    }

    // TODO publish events here

    if (cargo.isMisdirected()) {
      //domainEventNotifier.cargoWasMisdirected(cargo);
    }

    if (cargo.isUnloadedAtDestination()) {
      //domainEventNotifier.cargoHasArrived(cargo);
    }
    
  }

}
