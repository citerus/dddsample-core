package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class LocationTest extends TestCase {

  public void testEquals() {
    // Same location string - equal
    assertTrue(new Location("ATEST").equals(new Location("ATEST")));

    // Different location strings - not equal
    assertFalse(new Location("ATEST").equals(new Location("TESTB")));

    // Always equal to itself
    Location location = new Location("ATEST");
    assertTrue(location.equals(location));

    // Never equal to null
    assertFalse(location.equals(null));

    // Special NULL location is equal to itself
    assertTrue(Location.UNKNOWN.equals(Location.UNKNOWN));

    // These are all invalid UN locodes
    List<String> invalidUnlocodes = Arrays.asList(null, "", "   ", "SHRT", "LOOONG", "WhAt evR 1 12 !!#6/");
    for (String invalid : invalidUnlocodes) {
      failInvalidUnlocode(invalid);
    }
  }

  private void failInvalidUnlocode(String invalid) {
    try {
      new Location(invalid);
      fail(invalid + " should not be allowed as UN locode constructor argument");
    } catch (IllegalArgumentException expected) {}
  }
}
