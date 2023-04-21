package se.citerus.dddsample.domain.model.location;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnLocodeTest {

  @ValueSource(strings = {"AA234","AAA9B","AAAAA"})
  @ParameterizedTest
  public void shouldAllowCreationOfValidUnLoCodes(String input) {
    assertThat(new UnLocode(input)).isNotNull();
  }

  @ValueSource(strings = {"AAAA", "AAAAAA", "AAAA", "AAAAAA", "22AAA", "AA111"})
  @NullSource
  @EmptySource
  @ParameterizedTest
  public void shouldPreventCreationOfInvalidUnLoCodes(String input) {
    assertThatThrownBy(() -> new UnLocode(input)).isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
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

}
