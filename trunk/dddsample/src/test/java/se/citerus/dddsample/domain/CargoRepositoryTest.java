package se.citerus.dddsample.domain;

import junit.framework.TestCase;

public class CargoRepositoryTest extends TestCase {

  public void testFindByCargoId() {

    CargoRepository repository = new CargoRepositoryImpl();
    final TrackingId trackingId = new TrackingId("XYZ");
    Cargo cargo = repository.find(trackingId);

    assertSame(trackingId, cargo.trackingId());

  }
}