/**
 * Purpose
 * @author peter
 * @created 2009-sep-07
 * $Id$
 */
package se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory;

import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.service.TrackingIdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class TrackingIdGeneratorInMem implements TrackingIdGenerator {

  private static final AtomicLong SEQ = new AtomicLong(1);

  @Override
  public TrackingId nextTrackingId() {
    return new TrackingId(SEQ.getAndIncrement());
  }

}
