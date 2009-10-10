package se.citerus.dddsample.tracking.core.application.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;

@Service
public class MisdirectedNotifier {

  private CargoRepository cargoRepository;

  private static final Log LOG = LogFactory.getLog(MisdirectedNotifier.class);

  @Autowired
  public MisdirectedNotifier(final CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  @Transactional
  public void alertIfMisdirected(final TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);

    if (cargo.isMisdirected()) {
      /**
       * In a real system, some significant action would be taken
       * when this happens.
       */
      LOG.info("Cargo " + cargo + " is misdirected!");
    }
  }

  MisdirectedNotifier() {
  }
}
