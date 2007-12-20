package se.citerus.dddsample.domain;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import se.citerus.dddsample.repository.CargoRepository;

public class CargoRepositoryTest extends AbstractTransactionalDataSourceSpringContextTests {

  CargoRepository cargoRepository;

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  protected String[] getConfigLocations() {
    return new String[] {"context-persistence.xml"};
  }

  public void testFindByCargoId() {
    final TrackingId trackingId = new TrackingId("XYZ");
    Cargo cargo = cargoRepository.find(trackingId);

    assertEquals(trackingId, cargo.trackingId());
  }

}