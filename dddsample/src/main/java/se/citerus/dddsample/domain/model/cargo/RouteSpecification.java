package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.ValueObject;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.AbstractSpecification;

import java.util.Date;

/**
 * Route specification.
 * 
 */
public final class RouteSpecification extends AbstractSpecification<Itinerary> implements ValueObject<RouteSpecification> {
  private Location origin;
  private Location destination;
  private Date arrivalDeadline;

  /**
   * Factory for creatig a route specification for a cargo, from cargo
   * origin to cargo destination. Use for initial routing.
   *
   * @param cargo cargo
   * @param arrivalDeadline arrival deadline
   * @return A route specification for this cargo and arrival deadline
   */
  public static RouteSpecification forCargo(Cargo cargo, Date arrivalDeadline) {
    Validate.notNull(cargo);

    return new RouteSpecification(cargo.origin(), cargo.destination(), arrivalDeadline);
  }

  private RouteSpecification(Location origin, Location destination, Date arrivalDeadline) {
    Validate.noNullElements(new Object[] {origin, destination, arrivalDeadline});
    
    this.origin = origin;
    this.destination = destination;
    this.arrivalDeadline = arrivalDeadline;
  }

  public Location origin() {
    return origin;
  }

  public Location destination() {
    return destination;
  }

  public Date arrivalDeadline() {
    return arrivalDeadline;
  }

  @Override
  public boolean isSatisfiedBy(Itinerary itinerary) {
    // TODO implement
    return true;
  }

  @Override
  public boolean sameValueAs(RouteSpecification other) {
    return other != null && new EqualsBuilder().
      append(this.origin, other.origin).
      append(this.destination, other.destination).
      append(this.arrivalDeadline, other.arrivalDeadline).
      isEquals();
  }

  @Override
  public RouteSpecification copy() {
    return new RouteSpecification(origin, destination, arrivalDeadline);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RouteSpecification that = (RouteSpecification) o;

    return sameValueAs(that);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(this.origin).
      append(this.destination).
      append(this.arrivalDeadline).
      toHashCode();
  }

}
