package se.citerus.dddsample.tracking.core.domain.model.voyage;

import junit.framework.TestCase;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HAMBURG;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.STOCKHOLM;

import java.util.Date;

public class CarrierMovementTest extends TestCase {

  public void testConstructor() throws Exception {
    try {
      new CarrierMovement(null, null, new Date(), new Date());
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {
    }

    try {
      new CarrierMovement(null, null, new Date(), new Date());
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {
    }

    try {
      new CarrierMovement(STOCKHOLM, null, new Date(), new Date());
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {
    }

    try {
      new CarrierMovement(STOCKHOLM, HAMBURG, new Date(200), new Date(100));
      fail("Should not accept departure time after arrival time");
    } catch (IllegalArgumentException expected) {
    }

    try {
      new CarrierMovement(STOCKHOLM, HAMBURG, new Date(100), new Date(100));
      fail("Should not accept arrival time equal to departure time");
    } catch (IllegalArgumentException expected) {
    }

    try {
      new CarrierMovement(STOCKHOLM, STOCKHOLM, new Date(100), new Date(200));
      fail("Should not accept identical departure and arrival locations");
    } catch (IllegalArgumentException expected) {
    }

    // Ok
    new CarrierMovement(STOCKHOLM, HAMBURG, new Date(100), new Date(200));
  }

  public void testSameValueAsEqualsHashCode() throws Exception {
    CarrierMovement cm1 = new CarrierMovement(STOCKHOLM, HAMBURG, new Date(1), new Date(2));
    CarrierMovement cm2 = new CarrierMovement(STOCKHOLM, HAMBURG, new Date(1), new Date(2));
    CarrierMovement cm3 = new CarrierMovement(HAMBURG, STOCKHOLM, new Date(1), new Date(2));
    CarrierMovement cm4 = new CarrierMovement(HAMBURG, STOCKHOLM, new Date(1), new Date(2));

    assertTrue(cm1.sameValueAs(cm2));
    assertFalse(cm2.sameValueAs(cm3));
    assertTrue(cm3.sameValueAs(cm4));

    assertTrue(cm1.equals(cm2));
    assertFalse(cm2.equals(cm3));
    assertTrue(cm3.equals(cm4));

    assertTrue(cm1.hashCode() == cm2.hashCode());
    assertFalse(cm2.hashCode() == cm3.hashCode());
    assertTrue(cm3.hashCode() == cm4.hashCode());
  }

}
