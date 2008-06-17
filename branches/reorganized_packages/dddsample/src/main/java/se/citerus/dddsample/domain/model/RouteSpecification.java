package se.citerus.dddsample.domain.model;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

import se.citerus.dddsample.domain.model.Cargo;
import se.citerus.dddsample.domain.model.Itinerary;
import se.citerus.dddsample.domain.model.Location;

/**
 * Route specification
 */
public class RouteSpecification implements ValueObject<RouteSpecification>, Specification<Itinerary> {
  private Location origin;
  private Location destination;
  private Date arrivalDeadline;

  /**
   * Factory method.
   *
   * TODO just a suggestion - could be useful with RouteSpecification.witinOneWeek(cargo) etc
   * 
   * @param cargo cargo
   * @param arrivalDeadline arrival deadline
   * @return A route specification for this cargo and arrival deadline
   */
  public static RouteSpecification forCargo(Cargo cargo, Date arrivalDeadline) {
    Validate.notNull(cargo);
    Validate.notNull(arrivalDeadline);

    return new RouteSpecification(cargo.origin(), cargo.destination(), arrivalDeadline);
  }

  private RouteSpecification(Location origin, Location destination, Date arrivalDeadline) {
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

  public boolean isSatisfiedBy(Itinerary itinerary) {
    // TODO implement
    return true;
  }

  public boolean sameValueAs(RouteSpecification other) {
    return other != null && new EqualsBuilder().
      append(this.origin, other.origin).
      append(this.destination, other.destination).
      append(this.arrivalDeadline, other.arrivalDeadline).
      isEquals();
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
