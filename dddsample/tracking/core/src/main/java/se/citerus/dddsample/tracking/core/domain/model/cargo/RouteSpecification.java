package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.AbstractSpecification;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.Specification;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.Date;

/**
 * Route specification. Describes where a cargo orign and destination is,
 * and the arrival deadline.
 */
public class RouteSpecification extends ValueObjectSupport<RouteSpecification> {

  private final Location origin;
  private final Location destination;
  private final Date arrivalDeadline;

  // Delegate specifications
  private final Specification<Itinerary> notNull = new NotNullSpecification();
  private final Specification<Itinerary> sameOrigin = new SameOriginSpecification();
  private final Specification<Itinerary> sameDestination = new SameDestinationSpecification();
  private final Specification<Itinerary> meetsDeadline = new MeetsDeadlineSpecification();

  /**
   * @param origin          origin location - can't be the same as the destination
   * @param destination     destination location - can't be the same as the origin
   * @param arrivalDeadline arrival deadline
   */
  public RouteSpecification(final Location origin, final Location destination, final Date arrivalDeadline) {
    Validate.notNull(origin, "Origin is required");
    Validate.notNull(destination, "Destination is required");
    Validate.notNull(arrivalDeadline, "Arrival deadline is required");
    Validate.isTrue(!origin.sameAs(destination), "Origin and destination can't be the same: " + origin);

    this.origin = origin;
    this.destination = destination;
    this.arrivalDeadline = new Date(arrivalDeadline.getTime());
  }

  /**
   * @param itinerary itinerary
   * @return True if this route specification is satisfied by the itinerary,
   *         i.e. the cargo will be delivered according to requirements.
   */
  public boolean isSatisfiedBy(final Itinerary itinerary) {
    return notNull.and(sameOrigin).and(sameDestination).and(meetsDeadline).isSatisfiedBy(itinerary);
  }

  /**
   * @return Specified origin location.
   */
  public Location origin() {
    return origin;
  }

  /**
   * @return Specfied destination location.
   */
  public Location destination() {
    return destination;
  }

  /**
   * @return Arrival deadline.
   */
  public Date arrivalDeadline() {
    return new Date(arrivalDeadline.getTime());
  }

  /**
   * @param newDestination destination of new route specification
   * @return A copy of this route specification but with new destination
   */
  public RouteSpecification withDestination(final Location newDestination) {
    return new RouteSpecification(origin, newDestination, arrivalDeadline);
  }

  /**
   * @param newOrigin origin of new route specification
   * @return A copy of this route specification but with the new origin
   */
  public RouteSpecification withOrigin(final Location newOrigin) {
    return new RouteSpecification(newOrigin, destination, arrivalDeadline);
  }

  /**
   * @param newArrivalDeadline arrival deadline of new route specification
   * @return A copy of this route specification but with the new arrival deadline
   */
  public RouteSpecification withArrivalDeadline(final Date newArrivalDeadline) {
    return new RouteSpecification(origin, destination, newArrivalDeadline);
  }

  @Override
  public String toString() {
    return origin + " to " + destination + " by " + arrivalDeadline;
  }

  RouteSpecification() {
    // Needed by Hibernate
    origin = destination = null;
    arrivalDeadline = null;
  }

  // --- Private classes ---
  
  private final class NotNullSpecification extends FieldlessSpecification {
    @Override
    public final boolean isSatisfiedBy(final Itinerary itinerary) {
      return itinerary != null;
    }
  }

  private final class SameOriginSpecification extends FieldlessSpecification {
    @Override
    public final boolean isSatisfiedBy(final Itinerary itinerary) {
      return origin.sameAs(itinerary.initialLoadLocation());
    }
  }

  private final class SameDestinationSpecification extends FieldlessSpecification {
    @Override
    public final boolean isSatisfiedBy(final Itinerary itinerary) {
      return destination.sameAs(itinerary.finalUnloadLocation());
    }
  }

  private final class MeetsDeadlineSpecification extends FieldlessSpecification {
    @Override
    public final boolean isSatisfiedBy(final Itinerary itinerary) {
      return arrivalDeadline.after(itinerary.finalUnloadTime());
    }
  }

  private static abstract class FieldlessSpecification extends AbstractSpecification<Itinerary> {
    @Override
    public final boolean equals(final Object that) {
      return that != null && this.getClass().equals(that.getClass());
    }
    @Override
    public final int hashCode() {
      return this.getClass().hashCode();
    }
  }

}
