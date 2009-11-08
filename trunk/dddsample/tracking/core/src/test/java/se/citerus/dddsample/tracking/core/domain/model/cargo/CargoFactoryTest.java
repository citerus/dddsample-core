/**
 * Purpose
 * @author peter
 * @created 2009-aug-08
 * $Id$
 */
package se.citerus.dddsample.tracking.core.domain.model.cargo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.ROTTERDAM;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.LocationRepositoryInMem;

public class CargoFactoryTest {

  CargoFactory cargoFactory;

  @Before
  public void setup() {
    TrackingIdFactory stubFactory = new TrackingIdFactory() {
      @Override
      public TrackingId nextTrackingId() {
        return new TrackingId("ABC");
      }
    };
    cargoFactory = new CargoFactory(new LocationRepositoryInMem(), stubFactory);
  }

  @Test
  public void createNewCargo() {
    Cargo cargo = cargoFactory.newCargo(HONGKONG.unLocode(), ROTTERDAM.unLocode(), toDate("2009-07-01"));

    assertNotNull(cargo);
    assertEquals(cargo.trackingId(), new TrackingId("ABC"));
    assertEquals(cargo.routeSpecification(), new RouteSpecification(HONGKONG, ROTTERDAM, toDate("2009-07-01")));
  }

}
