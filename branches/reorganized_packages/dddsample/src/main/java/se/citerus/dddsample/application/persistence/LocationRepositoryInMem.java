package se.citerus.dddsample.application.persistence;

import se.citerus.dddsample.domain.model.Location;
import se.citerus.dddsample.domain.model.SampleLocations;
import se.citerus.dddsample.domain.model.UnLocode;
import se.citerus.dddsample.domain.repository.LocationRepository;

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
