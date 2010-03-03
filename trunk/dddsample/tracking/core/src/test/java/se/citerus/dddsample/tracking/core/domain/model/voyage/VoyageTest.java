package se.citerus.dddsample.tracking.core.domain.model.voyage;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.HONGKONG_TO_NEW_YORK;

public class VoyageTest {

  @Test
  public void locations() {
    Voyage voyage = HONGKONG_TO_NEW_YORK;
    assertEquals(asList(HONGKONG, HANGZOU, TOKYO, MELBOURNE, NEWYORK), voyage.locations());
  }

}
