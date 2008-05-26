package se.citerus.dddsample.domain;

import static se.citerus.dddsample.domain.StatusCode.*;

import java.util.*;

/**
 * The delivery history of a cargo.
 *
 * This is a value object.
 */
public final class DeliveryHistory {

  private final Set<HandlingEvent> events;

  public static final DeliveryHistory EMPTY_DELIVERY_HISTORY = new DeliveryHistory(Collections.EMPTY_SET);


  public DeliveryHistory(final Collection<HandlingEvent> events) {
    this.events = new HashSet<HandlingEvent>(events);
  }

  /**
   * Adds all HandlingEvent to the delivery history.
   *
   * @param events events to add
  public void addAllEvents(final Collection<HandlingEvent> events) {
    this.events.addAll(events);
  }
   */

  /**
   * Adds a HandlingEvent to the delivery history.
   *
   * @param event event to add.
  public void addEvent(final HandlingEvent event) {
    this.events.add(event);
  }
   */

  /**
   * @return An <b>unmodifiable</b> list of handling events, ordered by the time the events occured.
   */
  public List<HandlingEvent> eventsOrderedByCompletionTime() {
    final List<HandlingEvent> eventList = new ArrayList<HandlingEvent>(events);
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
      final List<HandlingEvent> orderedEvents = eventsOrderedByCompletionTime();
      return orderedEvents.get(orderedEvents.size() - 1);
    }
  }

  public StatusCode status() {
    if (lastEvent() == null)
      return NOT_RECEIVED;

    final HandlingEvent.Type type = lastEvent().type();
    
    switch (type) {
      case LOAD:
        return ONBOARD_CARRIER;

      case UNLOAD:
      case RECEIVE:
      case CUSTOMS:
        return IN_PORT;

      case CLAIM:
        return CLAIMED;

      default:
        return null;
    }
  }

  public Location currentLocation() {
    if (status().equals(IN_PORT)) {
      return lastEvent().location();
    } else {
      return null;
    }
  }

  public CarrierMovement currentCarrierMovement() {
    if (status().equals(ONBOARD_CARRIER)) {
      return lastEvent().carrierMovement();
    } else {
      return null;
    }
  }

  public boolean sameValueAs(DeliveryHistory other) {
    return other != null && events.equals(other.events);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final DeliveryHistory other = (DeliveryHistory) o;

    return sameValueAs(other);
  }

  @Override
  public int hashCode() {
    return events.hashCode();
  }

  /*
  DeliveryHistory() {
    // Needed by Hibernate
  }
  */

}
