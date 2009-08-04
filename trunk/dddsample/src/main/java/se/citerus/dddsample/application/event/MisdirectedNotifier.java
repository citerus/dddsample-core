/**
 * Purpose
 * @author peter
 * @created 2009-aug-03
 * $Id$
 */
package se.citerus.dddsample.application.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

public class MisdirectedNotifier {

  private CargoRepository cargoRepository;

  private static final Log LOG = LogFactory.getLog(MisdirectedNotifier.class);

  public MisdirectedNotifier(final CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  @Transactional
  public void alertIfMisdirected(final TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);

    if (cargo.delivery().isMisdirected()) {
      LOG.info("Cargo " + cargo + " is misdirected!");
    }
  }

  MisdirectedNotifier() {
  }
}
