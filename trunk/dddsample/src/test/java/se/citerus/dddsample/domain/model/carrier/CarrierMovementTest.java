package se.citerus.dddsample.domain.model.carrier;

import junit.framework.TestCase;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HAMBURG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;

import java.util.Date;

public class CarrierMovementTest extends TestCase {

  public void testConstructor() throws Exception {
    CarrierMovementId id = new CarrierMovementId("CAR001");

    try {
      new CarrierMovement(null, null, null, new Date(), new Date());
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}

    try {
      new CarrierMovement(id, null, null, new Date(), new Date());
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}

    try {
      new CarrierMovement(id, STOCKHOLM, null, new Date(), new Date());
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}

    // Legal
    new CarrierMovement(id, STOCKHOLM, HAMBURG, new Date(), new Date());
  }

  public void testSameValueAsEqualsHashCode() throws Exception {
    CarrierMovementId id1 = new CarrierMovementId("CAR1");
    CarrierMovementId id2a = new CarrierMovementId("CAR2");
    CarrierMovementId id2b = new CarrierMovementId("CAR2");

    CarrierMovement cm1 = new CarrierMovement(id1, STOCKHOLM, HAMBURG, new Date(), new Date());
    CarrierMovement cm2 = new CarrierMovement(id1, STOCKHOLM, HAMBURG, new Date(), new Date());
    CarrierMovement cm3 = new CarrierMovement(id2a, HAMBURG, STOCKHOLM, new Date(), new Date());
    CarrierMovement cm4 = new CarrierMovement(id2b, HAMBURG, STOCKHOLM, new Date(), new Date());

    assertTrue(cm1.sameIdentityAs(cm2));
    assertFalse(cm2.sameIdentityAs(cm3));
    assertTrue(cm3.sameIdentityAs(cm4));
    
    assertTrue(cm1.equals(cm2));
    assertFalse(cm2.equals(cm3));
    assertTrue(cm3.equals(cm4));

    assertTrue(cm1.hashCode() == cm2.hashCode());
    assertFalse(cm2.hashCode() == cm3.hashCode());
    assertTrue(cm3.hashCode() == cm4.hashCode());
  }

}
