package se.citerus.dddsample.application;

import se.citerus.dddsample.domain.model.cargo.TrackingId;

/**
 *
 */
public interface CargoLockingService {

  void assertLocked(TrackingId trackingId);

  void unlock(TrackingId trackingId);

  void lock(TrackingId trackingId);

}
