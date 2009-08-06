package se.citerus.dddsample.domain.model.cargo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.NEW_YORK_TO_DALLAS;
import se.citerus.dddsample.domain.model.voyage.Voyage;

public class LegTest {

  final Voyage voyage = NEW_YORK_TO_DALLAS;

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor() throws Exception {
    new Leg(null, null, null);
  }

  @Test
  public void legThatFollowsPartOfAVoyage() {
    Leg chicagoToDallas = new Leg(voyage, CHICAGO, DALLAS);

    assertEquals(chicagoToDallas.loadTime(), toDate("2008-10-24", "21:25"));
    assertEquals(chicagoToDallas.loadLocation(), CHICAGO);
    assertEquals(chicagoToDallas.unloadTime(), toDate("2008-10-25", "19:30"));
    assertEquals(chicagoToDallas.unloadLocation(), DALLAS);
  }

  @Test
  public void legThatFollowsAnEntireVoyage() {
    Leg chicagoToDallas = new Leg(voyage, NEWYORK, DALLAS);

    assertEquals(chicagoToDallas.loadTime(), toDate("2008-10-24", "07:00"));
    assertEquals(chicagoToDallas.loadLocation(), NEWYORK);
    assertEquals(chicagoToDallas.unloadTime(), toDate("2008-10-25", "19:30"));
    assertEquals(chicagoToDallas.unloadLocation(), DALLAS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void locationsInWrongOrder() {
    new Leg(voyage, DALLAS, CHICAGO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void endLocationNotOnVoyage() {
    new Leg(voyage, CHICAGO, HELSINKI);
  }

  @Test(expected = IllegalArgumentException.class)
  public void startLocationNotOnVoyage() {
    new Leg(voyage, HONGKONG, DALLAS);
  }

}
