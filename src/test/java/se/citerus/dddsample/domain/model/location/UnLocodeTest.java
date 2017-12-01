package se.citerus.dddsample.domain.model.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

public class UnLocodeTest {


  @Test
  public void testNew() {
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

  @Test
  public void testIdString() {
    assertThat(new UnLocode("AbcDe").idString()).isEqualTo("ABCDE");
  }

  @Test
  public void testEquals() {
    UnLocode allCaps = new UnLocode("ABCDE");
    UnLocode mixedCase = new UnLocode("aBcDe");

    assertThat(allCaps.equals(mixedCase)).isTrue();
    assertThat(mixedCase.equals(allCaps)).isTrue();
    assertThat(allCaps.equals(allCaps)).isTrue();

    assertThat(allCaps.equals(null)).isFalse();
    assertThat(allCaps.equals(new UnLocode("FGHIJ"))).isFalse();
  }

  @Test
  public void testHashCode() {
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
