package se.citerus.dddsample.domain.model.location;

import java.util.TimeZone;

import junit.framework.TestCase;

public class LocationTest extends TestCase {
	private static final TimeZone CET = TimeZone.getTimeZone("Europe/Amsterdam"); 
	
  public void testEquals() {
	  
    // Same UN locode - equal
    assertTrue(new Location(new UnLocode("ATEST"),"test-name", CET).
        equals(new Location(new UnLocode("ATEST"),"test-name", CET)));

    // Different UN locodes - not equal
    assertFalse(new Location(new UnLocode("ATEST"),"test-name", CET).
         equals(new Location(new UnLocode("TESTB"), "test-name", CET)));

    // Always equal to itself
    Location location = new Location(new UnLocode("ATEST"),"test-name", CET);
    assertTrue(location.equals(location));

    // Never equal to null
    assertFalse(location.equals(null));

    // Special UNKNOWN location is equal to itself
    assertTrue(Location.UNKNOWN.equals(Location.UNKNOWN));

    try {
      new Location(null, null, null);
      fail("Should not allow any null constructor arguments");
    } catch (IllegalArgumentException expected) {}
  }

}
