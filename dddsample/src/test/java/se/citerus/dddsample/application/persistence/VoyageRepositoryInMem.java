package se.citerus.dddsample.application.persistence;

import se.citerus.dddsample.domain.model.carrier.SampleVoyages;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;

public final class VoyageRepositoryInMem implements VoyageRepository {

  public Voyage find(VoyageNumber voyageNumber) {
    return SampleVoyages.lookup(voyageNumber);
  }
  
}
