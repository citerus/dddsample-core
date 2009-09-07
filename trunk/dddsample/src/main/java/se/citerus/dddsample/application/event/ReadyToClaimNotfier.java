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

public class ReadyToClaimNotfier {

  private CargoRepository cargoRepository;
  private static final Log LOG = LogFactory.getLog(ReadyToClaimNotfier.class);

  public ReadyToClaimNotfier(final CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  @Transactional
  public void alertIfReadyToClaim(final TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);
                                                      
    if (cargo.delivery().isUnloadedAtDestination()) {
      LOG.info("Cargo " + cargo + " is ready to be claimed");
    }
  }

  ReadyToClaimNotfier() {
  }
}
