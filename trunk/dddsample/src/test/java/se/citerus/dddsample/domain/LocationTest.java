package se.citerus.dddsample.domain;

import junit.framework.TestCase;

public class LocationTest extends TestCase {

  public void testEquals() {
    // Same location string - equal
    assertTrue(new Location("TEST").equals(new Location("TEST")));

    // Different location strings - not equal
    assertFalse(new Location("TEST").equals(new Location("ANOTHER_TEST")));

    // Always equal to itself
    Location location = new Location("TEST");
    assertTrue(location.equals(location));

    // Never equal to null
    assertFalse(location.equals(null));

    // Special NULL location is equal to itself
    assertTrue(Location.NULL.equals(Location.NULL));

    // No other location should be equal to the NULL location
    assertFalse(new Location(null).equals(Location.NULL));
    assertFalse(new Location("").equals(Location.NULL));
    assertFalse(new Location("   ").equals(Location.NULL));
    assertFalse(new Location("   FOO  BAR ").equals(Location.NULL));
  }

}
