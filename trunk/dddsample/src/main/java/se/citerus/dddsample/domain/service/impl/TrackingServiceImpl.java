package se.citerus.dddsample.domain.service.impl;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.service.TrackingService;

public class TrackingServiceImpl implements TrackingService {

  private final CargoRepository cargoRepository;
  private final Log logger = LogFactory.getLog(getClass());

  public TrackingServiceImpl(CargoRepository cargoRepository) {
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

    cargo.updateState();

    // Using a listener pattern and registering handlers for different scenarios
    // would be more in line with the open-close principle (?)
    if (cargo.isMisdirected()) {
      handleMisdirectedCargo(cargo);
    }

    if (cargo.isUnloadedAtDestination()) {
      notifyCustomerOfAvailability(cargo);
    }
  }

  private void notifyCustomerOfAvailability(Cargo cargo) {
    logger.info("Cargo " + cargo.trackingId() + " has been unloaded " +
                "at its final destination " + cargo.destination());
  }

  private void handleMisdirectedCargo(Cargo cargo) {
    logger.info("Cargo " + cargo.trackingId() + " has been misdirected. " +
                "Last event was " + cargo.deliveryHistory().lastEvent());
  }

}
