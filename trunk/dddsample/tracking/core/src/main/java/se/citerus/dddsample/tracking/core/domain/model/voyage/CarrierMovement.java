package se.citerus.dddsample.tracking.core.domain.model.voyage;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.shared.experimental.ValueObjectSupport;

import java.util.Date;


/**
 * A carrier movement is a vessel voyage from one location to another.
 */
public final class CarrierMovement extends ValueObjectSupport<CarrierMovement> {

  private final Location departureLocation;
  private final Location arrivalLocation;
  private final Date departureTime;
  private final Date arrivalTime;

  // Null object pattern 
  public static final CarrierMovement NONE = new CarrierMovement(
    Location.UNKNOWN, Location.UNKNOWN,
    new Date(0), new Date(0)
  );

  /**
   * Constructor.
   *
   * @param departureLocation location of departure
   * @param arrivalLocation   location of arrival
   * @param departureTime     time of departure
   * @param arrivalTime       time of arrival
   */
  // TODO make package local
  public CarrierMovement(Location departureLocation,
                         Location arrivalLocation,
                         Date departureTime,
                         Date arrivalTime) {
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
