package se.citerus.dddsample.tracking.core.domain.model.shared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.SEATTLE;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.loadOnto;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.pacific2;

public class HandlingActivityTest {

  @Test
  public void copy() {
    HandlingActivity activity = loadOnto(pacific2).in(SEATTLE);
    HandlingActivity copy = activity.copy();
    
    assertTrue(activity.sameValueAs(copy));
    assertFalse(activity == copy);
  }

}
