package se.citerus.dddsample.domain.model.cargo;

import se.citerus.dddsample.domain.model.ValueObject;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;

import java.util.*;

/**
 * The actual result of the cargo transportation, as opposed to
 * the customer requirement (RouteSpecification) and the plan (Itinerary). 
 *
 */
public class Delivery implements ValueObject<Delivery> {

  private Set<HandlingEvent> events;

  public static final Delivery EMPTY_DELIVERY = new Delivery(Collections.EMPTY_SET);

  Delivery(final Collection<HandlingEvent> events) {
    this.events = new HashSet<HandlingEvent>(events);
  }

  /**
   * @return An <b>unmodifiable</b> list of handling events, ordered by the time the events occured.
   */
  public List<HandlingEvent> history() {
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
      final List<HandlingEvent> orderedEvents = history();
      return orderedEvents.get(orderedEvents.size() - 1);
    }
  }

  /**
   * @return
   */
  public TransportStatus transportStatus() {
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

  /**
   * @return Current location, if  
   */
  public Location currentLocation() {
    if (transportStatus().equals(IN_PORT)) {
      return lastEvent().location();
    } else {
      return Location.UNKNOWN;
    }
  }

  /**
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  public Location lastKnownLocation() {
    final HandlingEvent lastEvent = lastEvent();
    if (lastEvent != null) {
      return lastEvent.location();
    } else {
      return Location.UNKNOWN;
    }
  }

  /**
   * @return Current voyage.
   */
  public Voyage currentVoyage() {
    if (transportStatus().equals(ONBOARD_CARRIER)) {
      return lastEvent().voyage();
    } else {
      return Voyage.NONE;
    }
  }

  @Override
  public boolean sameValueAs(Delivery other) {
    return other != null && events.equals(other.events);
  }

  @Override
  public Delivery copy() {
    final Set<HandlingEvent> eventsCopy = new HashSet<HandlingEvent>(events);

    return new Delivery(eventsCopy);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Delivery other = (Delivery) o;

    return sameValueAs(other);
  }

  @Override
  public int hashCode() {
    return events.hashCode();
  }

  Delivery() {
    // Needed by Hibernate
  }
}
