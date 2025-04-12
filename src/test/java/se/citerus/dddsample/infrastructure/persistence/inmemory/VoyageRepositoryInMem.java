package se.citerus.dddsample.infrastructure.persistence.inmemory;

import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.sampledata.SampleVoyages;

public final class VoyageRepositoryInMem implements VoyageRepository {

  public Voyage find(VoyageNumber voyageNumber) {
    return SampleVoyages.lookup(voyageNumber);
  }

  @Override
  public void store(Voyage voyage) {
    // noop
  }
}
