package se.citerus.dddsample.tracking.core.domain.model.location;

import junit.framework.TestCase;

import java.util.TimeZone;

public class LocationTest extends TestCase {
  private static final TimeZone CET = TimeZone.getTimeZone("Europe/Amsterdam");

  public void testEquals() {

    // Same UN locode - equal
    assertTrue(new Location(new UnLocode("ATEST"), "test-name", CET, null).
      equals(new Location(new UnLocode("ATEST"), "test-name", CET, null)));

    // Different UN locodes - not equal
    assertFalse(new Location(new UnLocode("ATEST"), "test-name", CET, null).
      equals(new Location(new UnLocode("TESTB"), "test-name", CET, null)));

    // Always equal to itself
    Location location = new Location(new UnLocode("ATEST"), "test-name", CET, null);
    assertTrue(location.equals(location));

    // Never equal to null
    assertFalse(location.equals(null));

    // Special NONE location is equal to itself
    assertTrue(Location.NONE.equals(Location.NONE));

    try {
      new Location(null, null, null, null);
      fail("Should not allow any null constructor arguments");
    } catch (IllegalArgumentException expected) {
    }
  }

}
