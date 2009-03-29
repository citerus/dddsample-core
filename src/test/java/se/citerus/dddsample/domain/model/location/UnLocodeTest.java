package se.citerus.dddsample.domain.model.location;

import junit.framework.TestCase;

public class UnLocodeTest extends TestCase {

  public void testNew() throws Exception {
    assertValid("AA234");
    assertValid("AAA9B");
    assertValid("AAAAA");
    
    assertInvalid("AAAA");
    assertInvalid("AAAAAA");
    assertInvalid("AAAA");
    assertInvalid("AAAAAA");
    assertInvalid("22AAA");
    assertInvalid("AA111");
    assertInvalid(null);
  }

  public void testIdString() throws Exception {
    assertEquals("ABCDE", new UnLocode("AbcDe").idString());
  }

  public void testEquals() throws Exception {
    UnLocode allCaps = new UnLocode("ABCDE");
    UnLocode mixedCase = new UnLocode("aBcDe");

    assertTrue(allCaps.equals(mixedCase));
    assertTrue(mixedCase.equals(allCaps));
    assertTrue(allCaps.equals(allCaps));

    assertFalse(allCaps.equals(null));
    assertFalse(allCaps.equals(new UnLocode("FGHIJ")));
  }

  public void testHashCode() throws Exception {
    UnLocode allCaps = new UnLocode("ABCDE");
    UnLocode mixedCase = new UnLocode("aBcDe");

    assertEquals(allCaps.hashCode(), mixedCase.hashCode());  
  }
  
  private void assertValid(String unlocode) {
    new UnLocode(unlocode);
  }

  private void assertInvalid(String unlocode) {
    try {
      new UnLocode(unlocode);
      fail("The combination [" + unlocode + "] is not a valid UnLocode");
    } catch (IllegalArgumentException expected) {}
  }

}
