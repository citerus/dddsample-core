package se.citerus.dddsample.domain;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import se.citerus.dddsample.repository.CargoRepository;

public class CargoRepositoryTest extends AbstractTransactionalDataSourceSpringContextTests {

  CargoRepository cargoRepository;

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  protected String[] getConfigLocations() {
    return new String[]{"context-persistence.xml"};
  }

  protected void onSetUpInTransaction() throws Exception {
    String[] testData = {
            "INSERT INTO locations (id, unlocode) VALUES (1, 'SESTO')",
            "INSERT INTO locations (id, unlocode) VALUES (2, 'CNHKG')",

            "INSERT INTO cargo (tracking_id, origin_location_fk, final_destination_location_fk) " +
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
  }

}