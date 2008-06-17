package se.citerus.dddsample.domain.service;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.Cargo;
import se.citerus.dddsample.domain.model.TrackingId;
import se.citerus.dddsample.domain.repository.CargoRepository;
import se.citerus.dddsample.domain.service.TrackingService;

public class TrackingServiceImpl implements TrackingService {

  private CargoRepository cargoRepository;

  private final Log logger = LogFactory.getLog(getClass());

  @Transactional(readOnly = true)
  public Cargo track(final TrackingId trackingId) {
    Validate.notNull(trackingId);

    return cargoRepository.find(trackingId);
  }

  @Transactional(readOnly = true)
  public void notify(final TrackingId trackingId) {
    Validate.notNull(trackingId);

    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      logger.warn("Can't notify listeners for non-existing cargo " + trackingId);
      return;
    }

    // TODO: more elaborate notifications, such as email to affected customer
    if (cargo.isMisdirected()) {
      logger.info("Cargo " + trackingId + " has been misdirected. " +
        "Last event was " + cargo.deliveryHistory().lastEvent());
    }
    if (cargo.isUnloadedAtDestination()) {
      logger.info("Cargo " + trackingId + " has been unloaded " +
        "at its final destination " + cargo.destination());
    }
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

}
