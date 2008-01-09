package se.citerus.dddsample.repository;

import java.util.Set;

import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;

public interface HandlingEventRepository {
  HandlingEvent find(String handlingEventId);
  void save(HandlingEvent event);
  Set<HandlingEvent> findByTrackingId(final TrackingId trackingId);
}
