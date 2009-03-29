package se.citerus.dddsample.infrastructure.persistence.inmemory;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlingEventRepositoryInMem implements HandlingEventRepository {

  private Map<TrackingId, List<HandlingEvent>> eventMap = new HashMap<TrackingId, List<HandlingEvent>>();

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

}
