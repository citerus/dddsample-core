package se.citerus.dddsample.infrastructure.persistence.hibernate;

import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.List;

public class LocationRepositoryTest extends AbstractRepositoryTest {
  private LocationRepository locationRepository;
  
  public void testFind() throws Exception {
    final UnLocode melbourne = new UnLocode("AUMEL");
    Location location = locationRepository.find(melbourne);
    assertNotNull(location);
    assertEquals(melbourne, location.unLocode());

    assertNull(locationRepository.find(new UnLocode("NOLOC")));
  }

  public void testFindAll() throws Exception {
    List<Location> allLocations = locationRepository.findAll();

    assertNotNull(allLocations);
    assertEquals(7, allLocations.size());
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }
}
