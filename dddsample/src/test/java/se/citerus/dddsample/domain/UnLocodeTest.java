package se.citerus.dddsample.domain;

import junit.framework.TestCase;

public class UnLocodeTest extends TestCase {

  public void testNew() throws Exception {
    assertValid("AA", "234");
    assertValid("AA", "A9B");
    assertValid("AA", "AAA");
    
    assertInvalid("A", "AAA");
    assertInvalid("AAA", "AAA");
    assertInvalid("AA", "AA");
    assertInvalid("AA", "AAAA");
    assertInvalid("22", "AAA");
    assertInvalid("AA", "111");
    assertInvalid(null, "AAA");
    assertInvalid("AA", null);
    assertInvalid(null, null);
  }

  public void testIdString() throws Exception {
    assertEquals("ABCDE", new UnLocode("Ab", "cDe").idString());
  }

  public void testEquals() throws Exception {
    UnLocode allCaps = new UnLocode("AB", "CDE");
    UnLocode mixedCase = new UnLocode("aB", "cDe");

    assertTrue(allCaps.equals(mixedCase));
    assertTrue(mixedCase.equals(allCaps));
    assertTrue(allCaps.equals(allCaps));

    assertFalse(allCaps.equals(null));
    assertFalse(allCaps.equals(new UnLocode("FG","HIJ")));
  }

  public void testHashCode() throws Exception {
    UnLocode allCaps = new UnLocode("AB", "CDE");
    UnLocode mixedCase = new UnLocode("aB", "cDe");

    assertEquals(allCaps.hashCode(), mixedCase.hashCode());  
  }
  
  private void assertValid(String countryCode, String locationCode) {
    new UnLocode(countryCode, locationCode);
  }

  private void assertInvalid(String countryCode, String locationCode) {
    try {
      new UnLocode(countryCode, locationCode);
      fail("The combination [" + countryCode + "," + locationCode + "] is not a valid UnLocode");
    } catch (IllegalArgumentException expected) {}
  }

}
