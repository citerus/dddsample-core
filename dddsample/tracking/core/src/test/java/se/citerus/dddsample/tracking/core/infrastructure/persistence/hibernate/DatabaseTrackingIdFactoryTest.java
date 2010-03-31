package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.springframework.beans.factory.annotation.Autowired;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;

/**
 *
 */
public class DatabaseTrackingIdFactoryTest extends AbstractRepositoryTest {

  @Autowired
  DatabaseTrackingIdFactory trackingIdFactory;

  public void testNext() throws Exception {
    TrackingId id1 = trackingIdFactory.nextTrackingId();
    TrackingId id2 = trackingIdFactory.nextTrackingId();
    assertNotNull(id1);
    assertNotNull(id2);
    assertFalse(id1.equals(id2));
  }

}
