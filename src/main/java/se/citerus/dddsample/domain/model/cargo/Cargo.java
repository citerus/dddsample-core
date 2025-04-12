package se.citerus.dddsample.domain.model.cargo;

import jakarta.persistence.*;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.DomainEntity;

import java.util.List;
import java.util.Objects;

/**
 * A Cargo. This is the central class in the domain model,
 * and it is the root of the Cargo-Itinerary-Leg-Delivery-RouteSpecification aggregate.
 *
 * A cargo is identified by a unique tracking id, and it always has an origin
 * and a route specification. The life cycle of a cargo begins with the booking procedure,
 * when the tracking id is assigned. During a (short) period of time, between booking
 * and initial routing, the cargo has no itinerary.
 *
 * The booking clerk requests a list of possible routes, matching the route specification,
 * and assigns the cargo to one route. The route to which a cargo is assigned is described
 * by an itinerary.
 *
 * A cargo can be re-routed during transport, on demand of the customer, in which case
 * a new route is specified for the cargo and a new route is requested. The old itinerary,
 * being a value object, is discarded and a new one is attached.
 *
 * It may also happen that a cargo is accidentally misrouted, which should notify the proper
 * personnel and also trigger a re-routing procedure.
 *
 * When a cargo is handled, the status of the delivery changes. Everything about the delivery
 * of the cargo is contained in the Delivery value object, which is replaced whenever a cargo
 * is handled by an asynchronous event triggered by the registration of the handling event.
 *
 * The delivery can also be affected by routing changes, i.e. when the route specification
 * changes, or the cargo is assigned to a new route. In that case, the delivery update is performed
 * synchronously within the cargo aggregate.
 *
 * The life cycle of a cargo ends when the cargo is claimed by the customer.
 *
 * The cargo aggregate, and the entire domain model, is built to solve the problem
 * of booking and tracking cargo. All important business rules for determining whether
 * or not a cargo is misdirected, what the current status of the cargo is (on board carrier,
 * in port etc), are captured in this aggregate.
 *
 */
@Entity(name = "Cargo")
@Table(name = "Cargo")
public class Cargo implements DomainEntity<Cargo> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "tracking_id", unique = true)
  private String trackingId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "origin_id")
  private Location origin;

  @Embedded
  private RouteSpecification routeSpecification;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "cargo_id")
  private List<Leg> itinerary; // TODO figure out if we can map an Itinerary object instead

  @Embedded
  private Delivery delivery;

  public Cargo(final TrackingId trackingId, final RouteSpecification routeSpecification) {
    Objects.requireNonNull(trackingId, "Tracking ID is required");
    Objects.requireNonNull(routeSpecification, "Route specification is required");

    this.trackingId = trackingId.idString();
    // Cargo origin never changes, even if the route specification changes.
    // However, at creation, cargo origin can be derived from the initial route specification.
    this.origin = routeSpecification.origin();
    this.routeSpecification = routeSpecification;

    this.delivery = Delivery.derivedFrom(
      this.routeSpecification, null, HandlingHistory.EMPTY
    );
  }

  public Cargo(TrackingId trackingId, RouteSpecification routeSpecification, Itinerary itinerary) {
    Objects.requireNonNull(trackingId, "Tracking ID is required");
    Objects.requireNonNull(routeSpecification, "Route specification is required");
    this.trackingId = trackingId.idString();
    this.origin = routeSpecification.origin();
    this.routeSpecification = routeSpecification;
    this.itinerary = itinerary.legs();

    this.delivery = Delivery.derivedFrom(
            this.routeSpecification, new Itinerary(this.itinerary), HandlingHistory.EMPTY
    );
  }

  /**
   * The tracking id is the identity of this entity, and is unique.
   *
   * @return Tracking id.
   */
  public TrackingId trackingId() {
    return new TrackingId(trackingId);
  }

  /**
   * @return Origin location.
   */
  public Location origin() {
    return origin;
  }

  /**
   * @return The delivery. Never null.
   */
  public Delivery delivery() {
    return delivery;
  }

  /**
   *
   * @return the id of the cargo, note that the id is not the tracking id.
   */
  public long id(){
    return id;
  }

  /**
   * @return The itinerary. Never null.
   */
  public Itinerary itinerary() {
    if (itinerary == null || itinerary.isEmpty()) {
      return Itinerary.EMPTY_ITINERARY;
    }
    return new Itinerary(itinerary);
  }

  /**
   * @return The route specification.
   */
  public RouteSpecification routeSpecification() {
    return routeSpecification;
  }

  /**
   * Specifies a new route for this cargo.
   *
   * @param routeSpecification route specification.
   */
  public void specifyNewRoute(final RouteSpecification routeSpecification) {
    Objects.requireNonNull(routeSpecification, "Route specification is required");

    this.routeSpecification = routeSpecification;
    Itinerary itineraryForRouting = this.itinerary != null && !this.itinerary.isEmpty() ? new Itinerary(this.itinerary) : null;
    // Handling consistency within the Cargo aggregate synchronously
    this.delivery = delivery.updateOnRouting(this.routeSpecification, itineraryForRouting);
  }

  /**
   * Attach a new itinerary to this cargo.
   *
   * @param itinerary an itinerary. May not be null.
   */
  public void assignToRoute(final Itinerary itinerary) {
    Objects.requireNonNull(itinerary, "Itinerary is required for assignment");

    this.itinerary = itinerary.legs();
    // Handling consistency within the Cargo aggregate synchronously
    this.delivery = delivery.updateOnRouting(this.routeSpecification, itinerary);
  }

  /**
   * Updates all aspects of the cargo aggregate status
   * based on the current route specification, itinerary and handling of the cargo.
   *
   * When either of those three changes, i.e. when a new route is specified for the cargo,
   * the cargo is assigned to a route or when the cargo is handled, the status must be
   * re-calculated.
   *
   * {@link RouteSpecification} and {@link Itinerary} are both inside the Cargo
   * aggregate, so changes to them cause the status to be updated <b>synchronously</b>,
   * but changes to the delivery history (when a cargo is handled) cause the status update
   * to happen <b>asynchronously</b> since {@link se.citerus.dddsample.domain.model.handling.HandlingEvent} is in a different aggregate.
   *
   * @param handlingHistory handling history
   */
  public void deriveDeliveryProgress(final HandlingHistory handlingHistory) {
    // Delivery is a value object, so we can simply discard the old one
    // and replace it with a new
    this.delivery = Delivery.derivedFrom(routeSpecification(), itinerary(), handlingHistory.filterOnCargo(new TrackingId(this.trackingId)));
  }

  @Override
  public boolean sameIdentityAs(final Cargo other) {
    return other != null && trackingId.equals(other.trackingId);
  }

  /**
   * @param object to compare
   * @return True if they have the same identity
   * @see #sameIdentityAs(Cargo)
   */
  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    final Cargo other = (Cargo) object;
    return sameIdentityAs(other);
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
    return trackingId;
  }

  protected Cargo() {
    // Needed by Hibernate
  }

}
