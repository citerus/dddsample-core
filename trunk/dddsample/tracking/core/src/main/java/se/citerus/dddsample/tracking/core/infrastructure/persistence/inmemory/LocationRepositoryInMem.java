package se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory;

import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;

import java.util.List;

public class LocationRepositoryInMem implements LocationRepository {

  public Location find(UnLocode unLocode) {
    for (Location location : SampleLocations.getAll()) {
      if (location.unLocode().equals(unLocode)) {
        return location;
      }
    }
    return null;
  }

  public List<Location> findAll() {
    return SampleLocations.getAll();
  }

}
