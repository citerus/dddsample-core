package se.citerus.dddsample.domain.model.voyage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HAMBURG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;

import java.util.Date;

import org.junit.Test;

public class CarrierMovementTest {

  @Test
  public void testConstructor() {
    try {
      new CarrierMovement(null, null, new Date(), new Date());
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}

    try {
      new CarrierMovement(STOCKHOLM, null, new Date(), new Date());
      fail("Should not accept null constructor arguments");
    } catch (IllegalArgumentException expected) {}

    // Legal
    new CarrierMovement(STOCKHOLM, HAMBURG, new Date(), new Date());
  }

  @Test
  public void testSameValueAsEqualsHashCode() {
    CarrierMovement cm1 = new CarrierMovement(STOCKHOLM, HAMBURG, new Date(), new Date());
    CarrierMovement cm2 = new CarrierMovement(STOCKHOLM, HAMBURG, new Date(), new Date());
    CarrierMovement cm3 = new CarrierMovement(HAMBURG, STOCKHOLM, new Date(), new Date());
    CarrierMovement cm4 = new CarrierMovement(HAMBURG, STOCKHOLM, new Date(), new Date());

    assertThat(cm1.sameValueAs(cm2)).isTrue();
    assertThat(cm2.sameValueAs(cm3)).isFalse();
    assertThat(cm3.sameValueAs(cm4)).isTrue();
    
    assertThat(cm1.equals(cm2)).isTrue();
    assertThat(cm2.equals(cm3)).isFalse();
    assertThat(cm3.equals(cm4)).isTrue();

    assertThat(cm1.hashCode() == cm2.hashCode()).isTrue();
    assertThat(cm2.hashCode() == cm3.hashCode()).isFalse();
    assertThat(cm3.hashCode() == cm4.hashCode()).isTrue();
  }

}
