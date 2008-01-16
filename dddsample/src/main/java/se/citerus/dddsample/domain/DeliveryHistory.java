package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.*;

/**
 * The delivery history of a cargo. This is a value object.
 *
 */
public class DeliveryHistory {

  private final Set<HandlingEvent> events;

  private static final HandlingEventByTimeComparator HANDLING_EVENT_COMPARATOR = new HandlingEventByTimeComparator();

  public DeliveryHistory() {
    this(Collections.<HandlingEvent>emptySet());
  }

  public DeliveryHistory(Collection<HandlingEvent> events) {
    this.events = new HashSet<HandlingEvent>(events);
  }

  /**
   * Adds the HandlingEvent to the sorted set.
   *
   * @param events events to add
   */
  public void addEvent(HandlingEvent... events) {
    this.events.addAll(Arrays.asList(events));
  }

  /**
   * @return An <b>unmodifiable</b> list of handling events, ordered by the time the events occured.
   */
  public List<HandlingEvent> eventsOrderedByTime() {
    List<HandlingEvent> eventList = new ArrayList<HandlingEvent>(events);
    Collections.sort(eventList, HANDLING_EVENT_COMPARATOR);
    return Collections.unmodifiableList(eventList);
  }

  /**
   * @return The last event of the delivery history, or null is history is empty.
   */
  public HandlingEvent lastEvent() {
    if (events.isEmpty()) {
      return null;
    } else {
      List<HandlingEvent> orderedEvents = eventsOrderedByTime();
      return orderedEvents.get(orderedEvents.size() - 1);
    }
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  private static class HandlingEventByTimeComparator implements Comparator<HandlingEvent> {
    public int compare(HandlingEvent o1, HandlingEvent o2) {
      return o1.completionTime().compareTo(o2.completionTime());
    }
  }
}
