package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

/**
 * A wrapper class that holds a sorted set of HandlingEvents. The set can not contain events with the same timestamp.
 * 
 */
@Embeddable
public class DeliveryHistory {

  @OneToMany
  private final Set<HandlingEvent> events = new HashSet<HandlingEvent>();

  private static final HandlingEventByTimeComparator HANDLING_EVENT_COMPARATOR = new HandlingEventByTimeComparator();

  /**
   * @return An <b>unmodifiable</b> list of handling events, ordered by the time the events occured.
   */
  public List<HandlingEvent> eventsOrderedByTime() {
    List<HandlingEvent> eventList = new ArrayList<HandlingEvent>(events);
    Collections.sort(eventList, HANDLING_EVENT_COMPARATOR);
    return Collections.unmodifiableList(eventList);
  }

  /**
   * Adds the HandlingEvent to the sorted set.
   * 
   * @throws IllegalArgumentException if an event is not unique. Uniquness are evaluated by checking that compareTo() not returns 0.
   * @param event
   */
  public void addEvent(HandlingEvent... event) {
      events.addAll(Arrays.asList(event));
  }

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
      return o1.getTime().compareTo(o2.getTime());
    }
  }
}
