package se.citerus.dddsample.domain.model.voyage;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.ValueObject;

import javax.persistence.*;
import java.util.Date;


/**
 * A carrier movement is a vessel voyage from one location to another.
 */
@Entity(name = "CarrierMovement")
@Table(name = "CarrierMovement")
public final class CarrierMovement implements ValueObject<CarrierMovement> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "arrival_location_id", nullable = false)
  public Location arrivalLocation;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "departure_location_id", nullable = false)
  public Location departureLocation;

  @Column(name = "arrival_time", nullable = false)
  public Date arrivalTime;

  @Column(name = "departure_time", nullable = false)
  public Date departureTime;

  // Null object pattern 
  public static final CarrierMovement NONE = new CarrierMovement(
    Location.UNKNOWN, Location.UNKNOWN,
    new Date(0), new Date(0)
  );

  /**
   * Constructor.
   *
   * @param departureLocation location of departure
   * @param arrivalLocation location of arrival
   * @param departureTime time of departure
   * @param arrivalTime time of arrival
   */
  // TODO make package local
  public CarrierMovement(Location departureLocation,
                         Location arrivalLocation,
                         Date departureTime,
                         Date arrivalTime) {
    //noinspection ObviousNullCheck
    Validate.noNullElements(new Object[]{departureLocation, arrivalLocation, departureTime, arrivalTime});
    this.departureTime = departureTime;
    this.arrivalTime = arrivalTime;
    this.departureLocation = departureLocation;
    this.arrivalLocation = arrivalLocation;
  }

  /**
   * @return Departure location.
   */
  public Location departureLocation() {
    return departureLocation;
  }

  /**
   * @return Arrival location.
   */
  public Location arrivalLocation() {
    return arrivalLocation;
  }

  /**
   * @return Time of departure.
   */
  public Date departureTime() {
    return new Date(departureTime.getTime());
  }

  /**
   * @return Time of arrival.
   */
  public Date arrivalTime() {
    return new Date(arrivalTime.getTime());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final CarrierMovement that = (CarrierMovement) o;

    return sameValueAs(that);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(this.departureLocation).
      append(this.departureTime).
      append(this.arrivalLocation).
      append(this.arrivalTime).
      toHashCode();
  }

  @Override
  public boolean sameValueAs(CarrierMovement other) {
    return other != null && new EqualsBuilder().
      append(this.departureLocation, other.departureLocation).
      append(this.departureTime, other.departureTime).
      append(this.arrivalLocation, other.arrivalLocation).
      append(this.arrivalTime, other.arrivalTime).
      isEquals();
  }

  CarrierMovement() {
    // Needed by Hibernate
  }

}
