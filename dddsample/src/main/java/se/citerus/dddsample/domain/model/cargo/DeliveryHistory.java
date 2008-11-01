package se.citerus.dddsample.domain.model.cargo;

import se.citerus.dddsample.domain.model.ValueObject;
import static se.citerus.dddsample.domain.model.cargo.StatusCode.*;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;

import java.util.*;

/**
 * The delivery history of a cargo.
 *
 * This is a value object.
 */
public final class DeliveryHistory implements ValueObject<DeliveryHistory> {

  private Set<HandlingEvent> events;

  public static final DeliveryHistory EMPTY_DELIVERY_HISTORY = new DeliveryHistory(Collections.EMPTY_SET);

  DeliveryHistory(final Collection<HandlingEvent> events) {
    this.events = new HashSet<HandlingEvent>(events);
  }

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
      return Location.UNKNOWN;
    }
  }

  public CarrierMovement currentCarrierMovement() {
    if (status().equals(ONBOARD_CARRIER)) {
      return lastEvent().carrierMovement();
    } else {
      return CarrierMovement.NONE;
    }
  }

  @Override
  public boolean sameValueAs(DeliveryHistory other) {
    return other != null && events.equals(other.events);
  }

  @Override
  public DeliveryHistory copy() {
    final Set<HandlingEvent> eventsCopy = new HashSet<HandlingEvent>(events);

    return new DeliveryHistory(eventsCopy);
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

  DeliveryHistory() {
    // Needed by Hibernate
  }

}
