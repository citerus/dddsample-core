package se.citerus.dddsample.tracking.core.domain.model.voyage;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.Date;


/**
 * A carrier movement is a vessel voyage from one location to another.
 */
public class CarrierMovement extends ValueObjectSupport<CarrierMovement> {

  private final Location departureLocation;
  private final Location arrivalLocation;
  private final Date departureTime;
  private final Date arrivalTime;

  /**
   * Constructor.
   *
   * @param departureLocation location of departure
   * @param arrivalLocation   location of arrival
   * @param departureTime     time of departure
   * @param arrivalTime       time of arrival
   */
  CarrierMovement(final Location departureLocation,
                  final Location arrivalLocation,
                  final Date departureTime,
                  final Date arrivalTime) {
    Validate.notNull(departureLocation, "Departure location is required");
    Validate.notNull(arrivalLocation, "Arrival location is required");
    Validate.notNull(departureTime, "Departure time is required");
    Validate.notNull(arrivalTime, "Arrival time is required");
    Validate.isTrue(arrivalTime.after(departureTime), "Arrival time must be after departure time");
    Validate.isTrue(!departureLocation.sameAs(arrivalLocation), "Departure location can't be the same as the arrival location");
    
    this.departureTime = new Date(departureTime.getTime());
    this.arrivalTime = new Date(arrivalTime.getTime());
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

  /**
   * @param newDepartureTime new departure time
   * @return A new CarrierMovement which is a copy of the old one but with a new departure time
   */
  CarrierMovement withDepartureTime(final Date newDepartureTime) {
    return new CarrierMovement(
      departureLocation,
      arrivalLocation,
      newDepartureTime,
      arrivalTime
    );
  }

  CarrierMovement() {
    // Needed by Hibernate
    arrivalLocation = departureLocation = null;
    arrivalTime = departureTime = null;
  }

}
