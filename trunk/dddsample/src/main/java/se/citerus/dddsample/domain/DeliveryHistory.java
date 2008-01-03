package se.citerus.dddsample.domain;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class DeliveryHistory {

  private final SortedSet<HandlingEvent> events = new TreeSet<HandlingEvent>();

  public SortedSet<HandlingEvent> getEvents() {
    return events;
  }

  public void addEvent(HandlingEvent event) {
    events.add(event);
  }

  public HandlingEvent last() {
    return events.isEmpty() ? null : events.last();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
