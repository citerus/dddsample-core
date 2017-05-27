package se.citerus.dddsample.domain.model.location;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(new UnLocode("AbcDe").idString()).isEqualTo("ABCDE");
  }

  public void testEquals() throws Exception {
    UnLocode allCaps = new UnLocode("ABCDE");
    UnLocode mixedCase = new UnLocode("aBcDe");

    assertThat(allCaps.equals(mixedCase)).isTrue();
    assertThat(mixedCase.equals(allCaps)).isTrue();
    assertThat(allCaps.equals(allCaps)).isTrue();

    assertThat(allCaps.equals(null)).isFalse();
    assertThat(allCaps.equals(new UnLocode("FGHIJ"))).isFalse();
  }

  public void testHashCode() throws Exception {
    UnLocode allCaps = new UnLocode("ABCDE");
    UnLocode mixedCase = new UnLocode("aBcDe");

    assertThat(mixedCase.hashCode()).isEqualTo(allCaps.hashCode());  
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
