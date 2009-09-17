/**
 * Purpose
 * @author peter
 * @created 2009-aug-08
 * $Id$
 */
package se.citerus.dddsample.domain.model.cargo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.ROTTERDAM;
import se.citerus.dddsample.infrastructure.persistence.TrackingIdGeneratorInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;

public class CargoFactoryTest {

  @Test
  public void createNewCargo() {
    CargoFactory cargoFactory = new CargoFactory(new LocationRepositoryInMem(), new TrackingIdGeneratorInMem());
    Cargo cargo = cargoFactory.newCargo(
      HONGKONG.unLocode(), ROTTERDAM.unLocode(), toDate("2009-07-01")
    );
    assertNotNull(cargo);
    assertNotNull(cargo.trackingId());
    assertEquals(cargo.routeSpecification(), new RouteSpecification(HONGKONG, ROTTERDAM, toDate("2009-07-01")));
  }

}
