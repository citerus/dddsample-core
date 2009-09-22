package se.citerus.dddsample.tracking.core.domain.model.cargo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.NEW_YORK_TO_DALLAS;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;

public class LegTest {

  final Voyage voyage = NEW_YORK_TO_DALLAS;

  @Test(expected = NullPointerException.class)
  public void testConstructor() throws Exception {
    Leg.deriveLeg(null, null, null);
  }

  @Test
  public void legThatFollowsPartOfAVoyage() {
    Leg chicagoToDallas = Leg.deriveLeg(voyage, CHICAGO, DALLAS);

    assertEquals(chicagoToDallas.loadTime(), toDate("2008-10-24", "21:25"));
    assertEquals(chicagoToDallas.loadLocation(), CHICAGO);
    assertEquals(chicagoToDallas.unloadTime(), toDate("2008-10-25", "19:30"));
    assertEquals(chicagoToDallas.unloadLocation(), DALLAS);
  }

  @Test
  public void legThatFollowsAnEntireVoyage() {
    Leg chicagoToDallas = Leg.deriveLeg(voyage, NEWYORK, DALLAS);

    assertEquals(chicagoToDallas.loadTime(), toDate("2008-10-24", "07:00"));
    assertEquals(chicagoToDallas.loadLocation(), NEWYORK);
    assertEquals(chicagoToDallas.unloadTime(), toDate("2008-10-25", "19:30"));
    assertEquals(chicagoToDallas.unloadLocation(), DALLAS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void locationsInWrongOrder() {
    Leg.deriveLeg(voyage, DALLAS, CHICAGO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void endLocationNotOnVoyage() {
    Leg.deriveLeg(voyage, CHICAGO, HELSINKI);
  }

  @Test(expected = IllegalArgumentException.class)
  public void startLocationNotOnVoyage() {
    Leg.deriveLeg(voyage, HONGKONG, DALLAS);
  }

}
