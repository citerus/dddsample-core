package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.UnLocode;

public class LocationRepositoryTest extends AbstractRepositoryTest {
  private LocationRepository locationRepository;
  
  public void testFind() throws Exception {
    final UnLocode melbourne = new UnLocode("AU", "MEL");
    Location location = locationRepository.find(melbourne);
    assertNotNull(location);
    assertEquals(melbourne, location.unLocode());
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }
}
