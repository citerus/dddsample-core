/**
 * Purpose
 * @author peter
 * @created 2009-sep-07
 * $Id$
 */
package se.citerus.dddsample.application;

import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CargoLockingServiceInMem implements CargoLockingService {

  private final Map<TrackingId, Lock> locks = new HashMap<TrackingId, Lock>();

  @Override
  public void assertLocked(final TrackingId trackingId) {
    final Lock lock = getLockFor(trackingId);
    if (!lock.tryLock()) {
      throw new RuntimeException("Lock not held");
    }
  }

  @Override
  public void unlock(final TrackingId trackingId) {
    final Lock lock = getLockFor(trackingId);
    lock.unlock();
  }

  @Override
  public void lock(final TrackingId trackingId) {
    final Lock lock = getLockFor(trackingId);
    if (!lock.tryLock()) {
      throw new RuntimeException("Could not lock");
    }
  }

  private Lock getLockFor(final TrackingId trackingId) {
    Lock lock = locks.get(trackingId);
    if (lock == null) {
      lock = new ReentrantLock();
      locks.put(trackingId, lock);
    }
    return lock;
  }

}
