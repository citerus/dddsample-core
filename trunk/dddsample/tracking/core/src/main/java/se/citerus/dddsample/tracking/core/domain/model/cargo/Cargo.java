package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.location.CustomsZone;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.shared.DomainObjectUtils;
import se.citerus.dddsample.tracking.core.domain.shared.Entity;

import java.util.Date;

/**
 * A Cargo. This is the central class in the domain model,
 * and it is the root of the Cargo-Itinerary-Leg-Delivery-RouteSpecification aggregate.
 * <p/>
 * A cargo is identified by a unique tracking id, and it always has an origin
 * and a route specification. The life cycle of a cargo begins with the booking procedure,
 * when the tracking id is assigned. During a (short) period of time, between booking
 * and initial routing, the cargo has no itinerary.
 * <p/>
 * The booking clerk requests a list of possible routes, matching the route specification,
 * and assigns the cargo to one route. The route to which a cargo is assigned is described
 * by an itinerary.
 * <p/>
 * A cargo can be re-routed during transport, on demand of the customer, in which case
 * a new route is specified for the cargo and a new route is requested. The old itinerary,
 * being a value object, is discarded and a new one is attached.
 * <p/>
 * It may also happen that a cargo is accidentally misrouted, which should notify the proper
 * personnel and also trigger a re-routing procedure.
 * <p/>
 * When a cargo is handled, the status of the delivery changes. Everything about the delivery
 * of the cargo is contained in the Delivery value object, which is replaced whenever a cargo
 * is handled by an asynchronous event triggered by the registration of the handling event.
 * <p/>
 * The delivery can also be affected by routing changes, i.e. when a the route specification
 * changes, or the cargo is assigned to a new route. In that case, the delivery update is performed
 * synchronously within the cargo aggregate.
 * <p/>
 * The life cycle of a cargo ends when the cargo is claimed by the customer.
 * <p/>
 * The cargo aggregate, and the entre domain model, is built to solve the problem
 * of booking and tracking cargo. All important business rules for determining whether
 * or not a cargo is misdirected, what the current status of the cargo is (on board carrier,
 * in port etc), are captured in this aggregate.
 */
public class Cargo implements Entity<Cargo> {

  private TrackingId trackingId;
  private RouteSpecification routeSpecification;
  private Itinerary itinerary;
  private Delivery delivery;
  private Projections projections;

  public Cargo(final TrackingId trackingId, final RouteSpecification routeSpecification) {
    Validate.notNull(trackingId, "Tracking ID is required");
    Validate.notNull(routeSpecification, "Route specification is required");

    this.trackingId = trackingId;
    this.routeSpecification = routeSpecification;
    this.delivery = Delivery.initial();
    this.projections = new Projections(delivery, itinerary, routeSpecification);
  }

  /**
   * The tracking id is the identity of this entity, and is unique.
   *
   * @return Tracking id.
   */
  public TrackingId trackingId() {
    return trackingId;
  }

  /**
   * @return The itinerary. Never null.
   */
  public Itinerary itinerary() {
    return DomainObjectUtils.nullSafe(this.itinerary, Itinerary.EMPTY_ITINERARY);
  }

  /**
   * @return The route specification.
   */
  public RouteSpecification routeSpecification() {
    return routeSpecification;
  }

  public Date estimatedTimeOfArrival() {
    return projections.estimatedTimeOfArrival();
  }

  public HandlingActivity nextExpectedActivity() {
    return projections.nextExpectedActivity();
  }

  public boolean isMisdirected() {
    return delivery.isMisdirected(itinerary, routeSpecification);
  }

  public TransportStatus transportStatus() {
    return delivery.transportStatus();
  }

  public RoutingStatus routingStatus() {
    return delivery.routingStatus(itinerary, routeSpecification);
  }

  public Voyage currentVoyage() {
    return delivery.currentVoyage();
  }

  public Location lastKnownLocation() {
    return delivery.lastKnownLocation();
  }

  /**
   * Specifies a new route for this cargo.
   *
   * @param routeSpecification route specification.
   */
  public void specifyNewRoute(final RouteSpecification routeSpecification) {
    Validate.notNull(routeSpecification, "Route specification is required");

    this.routeSpecification = routeSpecification;
    // Handling consistency within the Cargo aggregate synchronously
    this.projections = new Projections(delivery, itinerary, routeSpecification);
  }

  /**
   * Attach a new itinerary to this cargo.
   *
   * @param itinerary an itinerary. May not be null.
   */
  public void assignToRoute(final Itinerary itinerary) {
    Validate.notNull(itinerary, "Itinerary is required");

    this.itinerary = itinerary;
    // Handling consistency within the Cargo aggregate synchronously
    this.projections = new Projections(delivery, itinerary, routeSpecification);
  }

  public CustomsZone customsZone() {
    return routeSpecification.destination().customsZone();
  }


  public Location customsClearancePoint() {
    return customsZone().entryPoint(itinerary.locations());
  }

  public boolean isReadyToClaim() {
    return delivery.isUnloadedAtDestination(routeSpecification);
  }

  /**
   * Updates all aspects of the cargo aggregate status
   * based on the current route specification, itinerary and handling of the cargo.
   * <p/>
   * When either of those three changes, i.e. when a new route is specified for the cargo,
   * the cargo is assigned to a route or when the cargo is handled, the status must be
   * re-calculated.
   * <p/>
   * {@link RouteSpecification} and {@link Itinerary} are both inside the Cargo
   * aggregate, so changes to them cause the status to be updated <b>synchronously</b>,
   * but handling cause the status update to happen <b>asynchronously</b>
   * since {@link HandlingEvent} is in a different aggregate.
   *
   * @param handlingActivity handling activity
   */
  public void handled(final HandlingActivity handlingActivity) {
    Validate.notNull(handlingActivity, "Handling activity is required");

    // Delivery and Projections are value object, so they are replaced with new or derived ones
    this.delivery = Delivery.whenHandled(handlingActivity);
    this.projections = new Projections(delivery, itinerary, routeSpecification);
  }

  @Override
  public boolean sameAs(final Cargo other) {
    return other != null && trackingId().sameValueAs(other.trackingId());
  }

  /**
   * @param object to compare
   * @return True if they have the same identity
   * @see #sameAs(Cargo)
   */
  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    final Cargo other = (Cargo) object;
    return sameAs(other);
  }

  /**
   * @return Hash code of tracking id.
   */
  @Override
  public int hashCode() {
    return trackingId.hashCode();
  }

  @Override
  public String toString() {
    return trackingId + " (" + routeSpecification + ")";
  }

  Cargo() {
    // Needed by Hibernate
  }

  // Auto-generated surrogate key
  @SuppressWarnings("UnusedDeclaration")
  private Long id;

}
