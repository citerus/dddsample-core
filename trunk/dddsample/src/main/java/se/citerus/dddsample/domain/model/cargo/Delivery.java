package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.ValueObject;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.DomainObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.EMPTY_SET;
import static java.util.Collections.sort;
import java.util.HashSet;
import java.util.List;

/**
 * The actual result of the cargo transportation, as opposed to
 * the customer requirement (RouteSpecification) and the plan (Itinerary). 
 *
 */
public class Delivery implements ValueObject<Delivery> {

  public static final Delivery EMPTY_DELIVERY = Delivery.derivedFrom(EMPTY_SET);

  private TransportStatus transportStatus;
  private Location lastKnownLocation;
  private Voyage currentVoyage;
  private HandlingEvent lastEvent;

  /**
   * @return Transport status
   */
  public TransportStatus transportStatus() {
    return transportStatus;
  }

  /**
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  public Location lastKnownLocation() {
    return DomainObjectUtils.nullSafe(lastKnownLocation, Location.UNKNOWN);
  }

  /**
   * @return Current voyage.
   */
  public Voyage currentVoyage() {
    return DomainObjectUtils.nullSafe(currentVoyage, Voyage.NONE);
  }

  /**
   * @param handlingEvents handling events
   * @return An up to date Delivery derived from this collection of handling events.
   */
  static Delivery derivedFrom(final Collection<HandlingEvent> handlingEvents) {
    Validate.notNull(handlingEvents, "Handling events are required");
    
    final List<HandlingEvent> eventsByCompletionTime =
      new ArrayList<HandlingEvent>(new HashSet<HandlingEvent>(handlingEvents));
    sort(eventsByCompletionTime, HandlingEvent.BY_COMPLETION_TIME_COMPARATOR);

    final Delivery delivery = new Delivery();
    delivery.calculateLastEvent(eventsByCompletionTime);
    delivery.calculateTransportStatus();
    delivery.calculateLastKnownLocation();
    delivery.calculateCurrentVoyage();
    return delivery;
  }

  /**
   * @return The last event of the delivery history, or null is history is empty.
   */
  HandlingEvent lastEvent() {
    return lastEvent;
  }

  private void calculateLastEvent(final List<HandlingEvent> handlingEvents) {
    if (handlingEvents.isEmpty()) {
      lastEvent =  null;
    } else {
      lastEvent = handlingEvents.get(handlingEvents.size() - 1);
    }
  }

  private void calculateTransportStatus() {
    if (lastEvent == null) {
      transportStatus = NOT_RECEIVED;
      return;
    }

    switch (lastEvent.type()) {
      case LOAD:
        transportStatus = ONBOARD_CARRIER;
        break;
      case UNLOAD:
      case RECEIVE:
      case CUSTOMS:
        transportStatus = IN_PORT;
        break;
      case CLAIM:
        transportStatus = CLAIMED;
        break;
      default:
        transportStatus = UNKNOWN;
    }
  }

  private void calculateLastKnownLocation() {
    if (lastEvent != null) {
      lastKnownLocation = lastEvent.location();
    } else {
      lastKnownLocation = null;
    }
  }

  // TODO add currentCarrierMovement

  private void calculateCurrentVoyage() {
    if (transportStatus().equals(ONBOARD_CARRIER) && lastEvent != null) {
      currentVoyage = lastEvent.voyage();
    } else {
      currentVoyage = null;
    }
  }

  @Override
  public boolean sameValueAs(Delivery other) {
    return other != null && new EqualsBuilder().
      append(this.transportStatus, other.transportStatus).
      append(this.lastKnownLocation, other.lastKnownLocation).
      append(this.currentVoyage, other.currentVoyage).
      append(this.lastEvent, other.lastEvent).
      isEquals();
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
    return new HashCodeBuilder().
      append(transportStatus).
      append(lastKnownLocation).
      append(currentVoyage).
      append(lastEvent).
      toHashCode();
  }

  Delivery() {
    // Needed by Hibernate
  }

}
