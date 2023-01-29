package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import org.apache.commons.lang3.ObjectUtils;
import se.citerus.dddsample.domain.shared.ValueObject;

import javax.persistence.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;

import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;

/**
 * The actual transportation of the cargo, as opposed to
 * the customer requirement (RouteSpecification) and the plan (Itinerary). 
 *
 */
@Embeddable
public class Delivery implements ValueObject<Delivery> {

  @Column
  public boolean misdirected;

  @Column
  public Date eta;

  @Column(name = "calculated_at")
  public Date calculatedAt;

  @Column(name = "unloaded_at_dest")
  public boolean isUnloadedAtDestination;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "routing_status")
  public RoutingStatus routingStatus;

  @Embedded
  public HandlingActivity nextExpectedActivity;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "transport_status")
  public TransportStatus transportStatus;

  @ManyToOne
  @JoinColumn(name = "current_voyage_id")
  public Voyage currentVoyage;

  @ManyToOne()
  @JoinColumn(name = "last_known_location_id")
  public Location lastKnownLocation;

  @ManyToOne
  @JoinColumn(name = "last_event_id")
  public HandlingEvent lastEvent;

  private static final Date ETA_UNKNOWN = null;
  private static final HandlingActivity NO_ACTIVITY = null;

  /**
   * Creates a new delivery snapshot to reflect changes in routing, i.e.
   * when the route specification or the itinerary has changed
   * but no additional handling of the cargo has been performed.
   *
   * @param routeSpecification route specification
   * @param itinerary itinerary
   * @return An up to date delivery
   */
  Delivery updateOnRouting(RouteSpecification routeSpecification, Itinerary itinerary) {
    Validate.notNull(routeSpecification, "Route specification is required");

    return new Delivery(this.lastEvent, itinerary, routeSpecification);
  }

  /**
   * Creates a new delivery snapshot based on the complete handling history of a cargo,
   * as well as its route specification and itinerary.
   *
   * @param routeSpecification route specification
   * @param itinerary itinerary
   * @param handlingHistory delivery history
   * @return An up to date delivery.
   */
  static Delivery derivedFrom(RouteSpecification routeSpecification, Itinerary itinerary, HandlingHistory handlingHistory) {
    Validate.notNull(routeSpecification, "Route specification is required");
    Validate.notNull(handlingHistory, "Delivery history is required");

    final HandlingEvent lastEvent = handlingHistory.mostRecentlyCompletedEvent();

    return new Delivery(lastEvent, itinerary, routeSpecification);
  }

  /**
   * Internal constructor.
   *
   * @param lastEvent last event
   * @param itinerary itinerary
   * @param routeSpecification route specification
   */
  public Delivery(HandlingEvent lastEvent, Itinerary itinerary, RouteSpecification routeSpecification) {
    this.calculatedAt = new Date();
    this.lastEvent = lastEvent;

    this.misdirected = calculateMisdirectionStatus(itinerary);
    this.routingStatus = calculateRoutingStatus(itinerary, routeSpecification);
    this.transportStatus = calculateTransportStatus();
    this.lastKnownLocation = calculateLastKnownLocation();
    this.currentVoyage = calculateCurrentVoyage();
    this.eta = calculateEta(itinerary);
    this.nextExpectedActivity = calculateNextExpectedActivity(routeSpecification, itinerary);
    this.isUnloadedAtDestination = calculateUnloadedAtDestination(routeSpecification);
  }

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
      return Objects.requireNonNullElse(lastKnownLocation, Location.UNKNOWN);
  }

  /**
   * @return Current voyage.
   */
  public Voyage currentVoyage() {
      return Objects.requireNonNullElse(currentVoyage, Voyage.NONE);
  }

  /**
   * Check if cargo is misdirected.
   * <p/>
   * <ul>
   * <li>A cargo is misdirected if it is in a location that's not in the itinerary.
   * <li>A cargo with no itinerary can not be misdirected.
   * <li>A cargo that has received no handling events can not be misdirected.
   * </ul>
   *
   * @return <code>true</code> if the cargo has been misdirected,
   */
  public boolean isMisdirected() {
    return misdirected;
  }

  /**
   * @return Estimated time of arrival
   */
  public Date estimatedTimeOfArrival() {
    if (eta != ETA_UNKNOWN) {
      return new Date(eta.getTime());
    } else {
      return ETA_UNKNOWN;
    }
  }

  /**
   * @return The next expected handling activity.
   */
  public HandlingActivity nextExpectedActivity() {
    return nextExpectedActivity;
  }

  /**
   * @return True if the cargo has been unloaded at the final destination.
   */
  public boolean isUnloadedAtDestination() {
    return isUnloadedAtDestination;
  }

  /**
   * @return Routing status.
   */
  public RoutingStatus routingStatus() {
    return routingStatus;
  }

  /**
   * @return When this delivery was calculated.
   */
  public Date calculatedAt() {
    return new Date(calculatedAt.getTime());
  }

  // TODO add currentCarrierMovement (?)


  // --- Internal calculations below ---


  private TransportStatus calculateTransportStatus() {
    if (lastEvent == null) {
      return NOT_RECEIVED;
    }

    switch (lastEvent.type()) {
      case LOAD:
        return ONBOARD_CARRIER;
      case UNLOAD:
      case RECEIVE:
      case CUSTOMS:
        return IN_PORT;
      case CLAIM:
        return CLAIMED;
      default:
        return UNKNOWN;
    }
  }

  private Location calculateLastKnownLocation() {
    if (lastEvent != null) {
      return lastEvent.location();
    } else {
      return null;
    }
  }

  private Voyage calculateCurrentVoyage() {
    if (transportStatus().equals(ONBOARD_CARRIER) && lastEvent != null) {
      return lastEvent.voyage();
    } else {
      return null;
    }
  }

  private boolean calculateMisdirectionStatus(Itinerary itinerary) {
    if (lastEvent == null) {
      return false;
    } else {
      return !itinerary.isExpected(lastEvent);
    }
  }

  private Date calculateEta(Itinerary itinerary) {
    if (onTrack()) {
      return itinerary.finalArrivalDate();
    } else {
      return ETA_UNKNOWN;
    }
  }

  private HandlingActivity calculateNextExpectedActivity(RouteSpecification routeSpecification, Itinerary itinerary) {
    if (!onTrack()) return NO_ACTIVITY;

    if (lastEvent == null) return new HandlingActivity(HandlingEvent.Type.RECEIVE, routeSpecification.origin());

    switch (lastEvent.type()) {

      case LOAD:
        for (Leg leg : itinerary.legs()) {
          if (leg.loadLocation().sameIdentityAs(lastEvent.location())) {
            return new HandlingActivity(HandlingEvent.Type.UNLOAD, leg.unloadLocation(), leg.voyage());
          }
        }

        return NO_ACTIVITY;

      case UNLOAD:
        for (Iterator<Leg> it = itinerary.legs().iterator(); it.hasNext();) {
          final Leg leg = it.next();
          if (leg.unloadLocation().sameIdentityAs(lastEvent.location())) {
            if (it.hasNext()) {
              final Leg nextLeg = it.next();
              return new HandlingActivity(HandlingEvent.Type.LOAD, nextLeg.loadLocation(), nextLeg.voyage());
            } else {
              return new HandlingActivity(HandlingEvent.Type.CLAIM, leg.unloadLocation());
            }
          }
        }

        return NO_ACTIVITY;

      case RECEIVE:
        final Leg firstLeg = itinerary.legs().iterator().next();
        return new HandlingActivity(HandlingEvent.Type.LOAD, firstLeg.loadLocation(), firstLeg.voyage());

      case CLAIM:
      default:
        return NO_ACTIVITY;
    }
  }

  private RoutingStatus calculateRoutingStatus(Itinerary itinerary, RouteSpecification routeSpecification) {
    if (itinerary == null) {
      return NOT_ROUTED;
    } else {
      if (routeSpecification.isSatisfiedBy(itinerary)) {
        return ROUTED;
      } else {
        return MISROUTED;
      }
    }
  }

  private boolean calculateUnloadedAtDestination(RouteSpecification routeSpecification) {
    return lastEvent != null &&
      HandlingEvent.Type.UNLOAD.sameValueAs(lastEvent.type()) &&
      routeSpecification.destination().sameIdentityAs(lastEvent.location());
  }

  private boolean onTrack() {
    return routingStatus.equals(ROUTED) && !misdirected;
  }

  @Override
  public boolean sameValueAs(final Delivery other) {
    return other != null && new EqualsBuilder().
      append(this.transportStatus, other.transportStatus).
      append(this.lastKnownLocation, other.lastKnownLocation).
      append(this.currentVoyage, other.currentVoyage).
      append(this.misdirected, other.misdirected).
      append(this.eta, other.eta).
      append(this.nextExpectedActivity, other.nextExpectedActivity).
      append(this.isUnloadedAtDestination, other.isUnloadedAtDestination).
      append(this.routingStatus, other.routingStatus).
      append(this.calculatedAt, other.calculatedAt).
      append(this.lastEvent, other.lastEvent).
      isEquals();
  }

  @Override
  public boolean equals(final Object o) {
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
      append(misdirected).
      append(eta).
      append(nextExpectedActivity).
      append(isUnloadedAtDestination).
      append(routingStatus).
      append(calculatedAt).
      append(lastEvent).
      toHashCode();
  }

  Delivery() {
    // Needed by Hibernate
  }
}
