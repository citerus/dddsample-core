package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.*;

/**
 * The delivery history of a cargo.
 */
public class DeliveryHistory {

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
  DeliveryHistory() {
  }

  public StatusCode status() {
    if (lastEvent() == null)
      return StatusCode.notReceived;

    HandlingEvent.Type type = lastEvent().type();
    if (type == HandlingEvent.Type.LOAD)
      return StatusCode.onBoardCarrier;

    if (type == HandlingEvent.Type.UNLOAD)
      return StatusCode.inPort;

    if (type == HandlingEvent.Type.RECEIVE)
      return StatusCode.inPort;

    if (type == HandlingEvent.Type.CLAIM)
      return StatusCode.claimed;

    //TODO: What about Type.CUSTOMS?
    return null;
  }

  public Location currentLocation() {
    if (status().equals(StatusCode.inPort)) {
      return lastEvent().location();
    } else {
      return null;
    }
  }

  public CarrierMovement currentCarrierMovement() {
    if (status().equals(StatusCode.onBoardCarrier)) {
      return lastEvent().carrierMovement();
    } else {
      return null;
    }
  }

}
