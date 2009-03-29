package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;

public class LegTest extends TestCase {

  public void testConstructor() throws Exception {
    try {
      new Leg(null,null,null,null,null);
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}
  }
}
