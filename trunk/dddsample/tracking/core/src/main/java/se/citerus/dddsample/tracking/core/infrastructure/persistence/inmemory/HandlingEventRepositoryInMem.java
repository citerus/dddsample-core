package se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory;

import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.tracking.core.domain.model.handling.EventSequenceNumber;

import java.util.*;

public class HandlingEventRepositoryInMem implements HandlingEventRepository {

  private Map<TrackingId, List<HandlingEvent>> eventMap = new HashMap<TrackingId, List<HandlingEvent>>();
  private static final Comparator<HandlingEvent> BY_COMPLETION_TIME_DESC = new Comparator<HandlingEvent>() {
    @Override
    public int compare(HandlingEvent o1, HandlingEvent o2) {
      // Newest first
      return o2.completionTime().compareTo(o1.completionTime());
    }
  };

  @Override
  public HandlingEvent find(EventSequenceNumber eventSequenceNumber) {
    for (List<HandlingEvent> handlingEvents : eventMap.values()) {
      for (HandlingEvent handlingEvent : handlingEvents) {
        if (handlingEvent.sequenceNumber().sameValueAs(eventSequenceNumber)) {
          return handlingEvent;
        }
      }
    }

    return null;
  }

  @Override
  public void store(HandlingEvent event) {
    final TrackingId trackingId = event.cargo().trackingId();
    List<HandlingEvent> list = eventMap.get(trackingId);
    if (list == null) {
      list = new ArrayList<HandlingEvent>();
      eventMap.put(trackingId, list);
    }
    list.add(event);
  }

  @Override
  public HandlingHistory lookupHandlingHistoryOfCargo(Cargo cargo) {
    List<HandlingEvent> events = eventMap.get(cargo.trackingId());

    if (events == null) {
      return HandlingHistory.emptyForCargo(cargo);
    } else {
      return HandlingHistory.fromEvents(events);
    }
  }

  @Override
  public HandlingEvent mostRecentHandling(Cargo cargo) {
    List<HandlingEvent> handlingEvents = eventMap.get(cargo.trackingId());
    if (handlingEvents == null) {
      return null;
    }

    Collections.sort(handlingEvents, BY_COMPLETION_TIME_DESC);
    return handlingEvents.get(0);
  }

}
