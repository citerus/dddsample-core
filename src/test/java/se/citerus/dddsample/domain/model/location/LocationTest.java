package se.citerus.dddsample.domain.model.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

public class LocationTest {

  @Test
  public void testEquals() {
    // Same UN locode - equal
    assertThat(new Location(new UnLocode("ATEST"),"test-name").
        equals(new Location(new UnLocode("ATEST"),"test-name"))).isTrue();

    // Different UN locodes - not equal
    assertThat(new Location(new UnLocode("ATEST"),"test-name").
         equals(new Location(new UnLocode("TESTB"), "test-name"))).isFalse();

    // Always equal to itself
    Location location = new Location(new UnLocode("ATEST"),"test-name");
    assertThat(location.equals(location)).isTrue();

    // Never equal to null
    assertThat(location.equals(null)).isFalse();

    // Special UNKNOWN location is equal to itself
    assertThat(Location.UNKNOWN.equals(Location.UNKNOWN)).isTrue();

    try {
      new Location(null, null);
      fail("Should not allow any null constructor arguments");
    } catch (IllegalArgumentException expected) {}
  }

}
