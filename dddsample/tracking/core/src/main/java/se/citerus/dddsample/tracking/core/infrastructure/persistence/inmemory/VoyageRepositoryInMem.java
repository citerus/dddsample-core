package se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory;

import se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

public final class VoyageRepositoryInMem implements VoyageRepository {

  public Voyage find(VoyageNumber voyageNumber) {
    return SampleVoyages.lookup(voyageNumber);
  }

}
