package se.citerus.dddsample.domain;

import java.util.SortedSet;
import java.util.TreeSet;

public class DeliveryHistory {

  private TreeSet<HandlingEvent> events;

  public DeliveryHistory() {
    this.events = new TreeSet<HandlingEvent>();
  }

  public SortedSet<HandlingEvent> events() {
    return events;
  }

  public void add(HandlingEvent event) {
    events.add(event);
  }

  public HandlingEvent last() {
    return events.last();
  }
}
