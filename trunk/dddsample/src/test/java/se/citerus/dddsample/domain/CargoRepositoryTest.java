package se.citerus.dddsample.domain;

import junit.framework.TestCase;
import se.citerus.dddsample.repository.CargoRepositoryInMem;

public class CargoRepositoryTest extends TestCase {

  public void testFindByCargoId() throws Exception {
    CargoRepository repository = new CargoRepositoryInMem();
    final TrackingId trackingId = new TrackingId("XYZ");
    Cargo cargo = repository.find(trackingId);

    assertEquals(trackingId, cargo.trackingId());

  }

}