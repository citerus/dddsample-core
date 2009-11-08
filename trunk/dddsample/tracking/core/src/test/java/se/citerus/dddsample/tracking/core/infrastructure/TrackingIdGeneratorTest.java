package se.citerus.dddsample.tracking.core.infrastructure;

import junit.framework.Assert;
import junit.framework.TestCase;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingIdFactory;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.TrackingIdFactoryInMem;

public class TrackingIdGeneratorTest extends TestCase {

  TrackingIdFactory trackingIdFactory = new TrackingIdFactoryInMem();

  public void testNextTrackingId() {
    TrackingId trackingId = trackingIdFactory.nextTrackingId();
    Assert.assertNotNull(trackingId);

    TrackingId trackingId2 = trackingIdFactory.nextTrackingId();
    Assert.assertNotNull(trackingId2);
    Assert.assertFalse(trackingId.equals(trackingId2));
  }

}
