package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.UnLocode;

import java.util.List;

public class LocationRepositoryTest extends AbstractRepositoryTest {
  private LocationRepository locationRepository;
  
  public void testFind() throws Exception {
    final UnLocode melbourne = new UnLocode("AU", "MEL");
    Location location = locationRepository.find(melbourne);
    assertNotNull(location);
    assertEquals(melbourne, location.unLocode());

    assertNull(locationRepository.find(new UnLocode("NO","LOC")));
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
