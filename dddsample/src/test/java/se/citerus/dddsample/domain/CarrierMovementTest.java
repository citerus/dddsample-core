package se.citerus.dddsample.domain;

import junit.framework.TestCase;

public class CarrierMovementTest extends TestCase {

  Location stockholm = new Location(new UnLocode("SE", "STO"), "Stockholm");
  Location hamburg = new Location(new UnLocode("DE", "HAM"), "Hamburg");

  public void testConstructor() throws Exception {
    CarrierMovementId id = new CarrierMovementId("CAR001");

    try {
      new CarrierMovement(null, null, null);
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}

    try {
      new CarrierMovement(id, null, null);
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}

    try {
      new CarrierMovement(id, stockholm, null);
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}

    // Legal
    new CarrierMovement(id, stockholm, hamburg);
  }

  public void testSameValueAsEqualsHashCode() throws Exception {
    CarrierMovementId id1 = new CarrierMovementId("CAR1");
    CarrierMovementId id2a = new CarrierMovementId("CAR2");
    CarrierMovementId id2b = new CarrierMovementId("CAR2");

    CarrierMovement cm1 = new CarrierMovement(id1, stockholm, hamburg);
    CarrierMovement cm2 = new CarrierMovement(id1, stockholm, hamburg);
    CarrierMovement cm3 = new CarrierMovement(id2a, hamburg, stockholm);
    CarrierMovement cm4 = new CarrierMovement(id2b, hamburg, stockholm);

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
