package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;

public class TrackingIdTest extends TestCase {

  public void testConstructor() throws Exception {
    try {
      new TrackingId(null);
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}
  }

}
