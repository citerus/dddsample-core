/**
 * Purpose
 * @author peter
 * @created 2009-aug-03
 * $Id$
 */
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
public class ReadyToClaimNotfier {

  private CargoRepository cargoRepository;
  private static final Log LOG = LogFactory.getLog(ReadyToClaimNotfier.class);

  @Autowired
  public ReadyToClaimNotfier(final CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  @Transactional
  public void alertIfReadyToClaim(final TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);
                                                      
    if (cargo.isReadyToClaim()) {
      /**
       * At this point, a real system would probably send an email or SMS
       * or something, but we simply log a message.
       */
      LOG.info("Cargo " + cargo + " is ready to be claimed");
    }
  }

  ReadyToClaimNotfier() {
  }
}
