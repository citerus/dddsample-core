package se.citerus.dddsample.domain.model.voyage;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A voyage schedule.
 */
public class Schedule implements ValueObject<Schedule> {

  private List<CarrierMovement> carrierMovements;

  public static final Schedule EMPTY = new Schedule();

  Schedule(final List<CarrierMovement> carrierMovements) {
    Validate.notNull(carrierMovements);
    Validate.noNullElements(carrierMovements);
    Validate.notEmpty(carrierMovements);

    this.carrierMovements = carrierMovements;
  }

  /**
   * @return Carrier movements.
   */
  public List<CarrierMovement> carrierMovements() {
    return Collections.unmodifiableList(carrierMovements);
  }

  /**
   * @param location location of departure
   * @return Date of departure from this location, or null if it's not part of the voyage.
   */
  public Date departureTimeAt(final Location location) {
    for (CarrierMovement movement : carrierMovements) {
      if (movement.departureLocation().sameIdentityAs(location)) {
        return movement.departureTime();
      }
    }
    return null;
  }

  /**
   * @param location location of arrival
   * @return Date of arrival at this location, or null if it's not part of the voyage.
   */
  public Date arrivalTimeAt(final Location location) {
    for (CarrierMovement movement : carrierMovements) {
      if (movement.arrivalLocation().sameIdentityAs(location)) {
        return movement.arrivalTime();
      }
    }
    return null;
  }

  @Override
  public boolean sameValueAs(final Schedule other) {
    return other != null && this.carrierMovements.equals(other.carrierMovements);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Schedule that = (Schedule) o;

    return sameValueAs(that);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this.carrierMovements).toHashCode();
  }

  Schedule() {
    // Needed by Hibernate
  }
}
