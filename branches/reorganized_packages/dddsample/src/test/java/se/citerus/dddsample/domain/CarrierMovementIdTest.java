package se.citerus.dddsample.domain;

import junit.framework.TestCase;

public class CarrierMovementIdTest extends TestCase {

  public void testConstructor() throws Exception {
    try {
      new CarrierMovementId(null);
      fail("Should not accept null constructor argument");
    } catch (IllegalArgumentException expected) {}
  }

}
