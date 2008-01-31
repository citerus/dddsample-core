package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Location;

public class LocationRepositoryTest extends AbstractRepositoryTest {
  private LocationRepository locationRepository;
  
  public void testFind() throws Exception {
    Location location = locationRepository.find("AUMEL");
    assertNotNull(location);
    assertEquals("AUMEL", location.unlocode());
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }
}
