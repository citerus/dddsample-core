package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.Leg.deriveLeg;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.loadOnto;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.unloadOff;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.continental2;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.pacific1;

@RunWith(JUnit4ClassRunner.class)
public class LegActivityMatchTest {

  @Test
  public void compareMatches() {
    Itinerary itinerary = new Itinerary(
      deriveLeg(pacific1, TOKYO, LONGBEACH),
      deriveLeg(continental2, LONGBEACH, DALLAS)
    );

    LegActivityMatch startMatch = LegActivityMatch.match(
      deriveLeg(pacific1, TOKYO, LONGBEACH),
      loadOnto(pacific1).in(TOKYO), itinerary);

    assertThat(startMatch.handlingActivity(), equalTo(loadOnto(pacific1).in(TOKYO)));
    assertThat(startMatch.leg(), equalTo(deriveLeg(pacific1, TOKYO, LONGBEACH)));

    LegActivityMatch endMatch = LegActivityMatch.match(
      deriveLeg(pacific1, TOKYO, LONGBEACH),
      unloadOff(pacific1).in(LONGBEACH), itinerary);

    assertThat(endMatch.handlingActivity(), equalTo(unloadOff(pacific1).in(LONGBEACH)));
    assertThat(endMatch.leg(), equalTo(deriveLeg(pacific1, TOKYO, LONGBEACH)));

    LegActivityMatch nextMatch = LegActivityMatch.match(
      deriveLeg(continental2, LONGBEACH, DALLAS),
      loadOnto(continental2).in(LONGBEACH), itinerary);

    assertThat(nextMatch.handlingActivity(), equalTo(loadOnto(continental2).in(LONGBEACH)));
    assertThat(nextMatch.leg(), equalTo(deriveLeg(continental2, LONGBEACH, DALLAS)));

    assertThat(startMatch.compareTo(endMatch), is(-1));
    assertThat(endMatch.compareTo(startMatch), is(1));
    assertThat(endMatch.compareTo(nextMatch), is(-1));
    assertThat(nextMatch.compareTo(endMatch), is(1));

    assertThat(startMatch.compareTo(startMatch), is(0));
  }

}
