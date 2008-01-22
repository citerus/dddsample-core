package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.*;

/**
 * The delivery history of a cargo.
 *
 */
@Embeddable
public class DeliveryHistory {

  @OneToMany
  @JoinColumn(name = "cargo_id")
  private Set<HandlingEvent> events = new HashSet<HandlingEvent>();

  /**
   * Adds all HandlingEvent to the delivery history.
   *
   * @param events events to add
   */
  public void addAllEvents(Collection<HandlingEvent> events) {
    this.events.addAll(events);
  }

  /**
   * Adds a HandlingEvent to the delivery history.
   *
   * @param event event to add.
   */
  public void addEvent(HandlingEvent event) {
    this.events.add(event);
  }
  
  /**
   * @return An <b>unmodifiable</b> list of handling events, ordered by the time the events occured.
   */
  public List<HandlingEvent> eventsOrderedByCompletionTime() {
    List<HandlingEvent> eventList = new ArrayList<HandlingEvent>(events);
    Collections.sort(eventList, HandlingEvent.BY_COMPLETION_TIME_COMPARATOR);
    return Collections.unmodifiableList(eventList);
  }

  /**
   * @return The last event of the delivery history, or null is history is empty.
   */
  public HandlingEvent lastEvent() {
    if (events.isEmpty()) {
      return null;
    } else {
      List<HandlingEvent> orderedEvents = eventsOrderedByCompletionTime();
      return orderedEvents.get(orderedEvents.size() - 1);
    }
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  // Needed by Hibernate
  DeliveryHistory() {}

}
