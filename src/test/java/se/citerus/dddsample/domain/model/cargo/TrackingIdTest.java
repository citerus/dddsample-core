package se.citerus.dddsample.domain.model.cargo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TrackingIdTest {

  @Test
  public void testConstructor() {
    assertThatThrownBy(() -> new TrackingId(null)).isInstanceOf(NullPointerException.class);
  }

}
