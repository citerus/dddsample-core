package se.citerus.dddsample.domain.model.voyage;

import junit.framework.TestCase;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.util.Date;

public class VoyageTest extends TestCase {


  public void testSchedule() {
    Voyage transcontinental = new Voyage.Builder(new VoyageNumber("4567"),
      LONGBEACH).
      addMovement(CHICAGO, new Date(), new Date()).
      addMovement(NEWYORK, new Date(), new Date()).
      build();
  }

}
