package se.citerus.dddsample.tracking.core.infrastructure;

import junit.framework.Assert;
import junit.framework.TestCase;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingIdGenerator;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.TrackingIdGeneratorInMem;

public class TrackingIdGeneratorTest extends TestCase {

  TrackingIdGenerator trackingIdGenerator = new TrackingIdGeneratorInMem();

  public void testNextTrackingId() {
    TrackingId trackingId = trackingIdGenerator.nextTrackingId();
    Assert.assertNotNull(trackingId);

    TrackingId trackingId2 = trackingIdGenerator.nextTrackingId();
    Assert.assertNotNull(trackingId2);
    Assert.assertFalse(trackingId.equals(trackingId2));
  }

}
