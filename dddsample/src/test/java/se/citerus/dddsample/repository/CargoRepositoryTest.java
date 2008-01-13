package se.citerus.dddsample.repository;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;

public class CargoRepositoryTest extends AbstractTransactionalDataSourceSpringContextTests {

  CargoRepository cargoRepository;

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  protected String[] getConfigLocations() {
    // TODO: when we move to persistent repositories, use main context-persistence.xml
    return new String[]{"test-context-persistence.xml"};
  }

  protected void onSetUpInTransaction() throws Exception {
    String[] testData = {
            "INSERT INTO Location (id, unlocode) VALUES (1, 'SESTO')",
            "INSERT INTO Location (id, unlocode) VALUES (2, 'CNHKG')",

            "INSERT INTO Cargo (id, origin_id, finalDestination_id) " +
            "VALUES ('XYZ', 1, 2)"
    };
    jdbcTemplate.batchUpdate(testData);
  }

  public void testFindByCargoId() {
    final TrackingId trackingId = new TrackingId("XYZ");
    final Location origin = new Location("SESTO");
    final Location finalDestination = new Location("CNHKG");

    Cargo cargo = cargoRepository.find(trackingId);

    assertEquals(trackingId, cargo.trackingId());
    assertEquals(origin, cargo.origin());
    assertEquals(finalDestination, cargo.finalDestination());
    assertEquals(Location.UNKNOWN, cargo.getCurrentLocation());
  }

}