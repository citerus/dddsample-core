package se.citerus.dddsample.tracking.core.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;

public class CustomsZoneTest extends TestCase {

  public void testIncludes() {
    assertTrue(US.includes(DALLAS));
    assertFalse(EU.includes(NEWYORK));
  }

  public void testEntryPoint() {
    assertEquals(LONGBEACH, US.entryPoint(SHANGHAI, LONGBEACH, CHICAGO));
  }

  public void testClearancePoint() {
    assertEquals(LONGBEACH, US.entryPoint(SHANGHAI, LONGBEACH, CHICAGO));
  }

}
