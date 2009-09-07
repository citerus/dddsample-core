/**
 * Purpose
 * @author peter
 * @created 2009-aug-04
 * $Id$
 */
package se.citerus.dddsample.application.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import java.util.List;

public final class ItineraryUpdater {

  private VoyageRepository voyageRepository;
  private CargoRepository cargoRepository;
  private static final Log LOG = LogFactory.getLog(ItineraryUpdater.class);

  public ItineraryUpdater(final VoyageRepository voyageRepository, final CargoRepository cargoRepository) {
    this.voyageRepository = voyageRepository;
    this.cargoRepository = cargoRepository;
  }

  @Transactional
  public void updateItineraries(final VoyageNumber voyageNumber) {
    final Voyage voyage = voyageRepository.find(voyageNumber);
    final List<Cargo> affectedCargos = cargoRepository.findCargosOnVoyage(voyage);
    for (final Cargo cargo : affectedCargos) {
      final Itinerary newItinerary = cargo.itinerary().withRescheduledVoyage(voyage);
      cargo.assignToRoute(newItinerary);
      LOG.info("Updated itinerary of cargo " + cargo);
    }
  }

}
