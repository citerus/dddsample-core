package se.citerus.dddsample.domain;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A wrapper class that holds a sorted set of HandlingEvents. The set can not contain events with the same timestamp.
 * 
 */
public class DeliveryHistory {

  private final SortedSet<HandlingEvent> events = new TreeSet<HandlingEvent>();

  public SortedSet<HandlingEvent> getEvents() {
    return events;
  }

  /**
   * Adds the HandlingEvent to the sorted set.
   * 
   * @throws IllegalArgumentException if an event is not unique. Uniquness are evaluated by checking that compareTo() not returns 0.
   * @param event
   */
  public void addEvent(HandlingEvent event) {
    if (!events.add(event)){
      throw new IllegalArgumentException("HandlingEvent are not evaluated to be unique");
    }
  }

  public HandlingEvent last() {
    return events.isEmpty() ? null : events.last();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
