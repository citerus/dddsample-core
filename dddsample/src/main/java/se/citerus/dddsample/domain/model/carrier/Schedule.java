package se.citerus.dddsample.domain.model.carrier;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.ValueObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A voyage schedule.
 * 
 */
public class Schedule implements ValueObject<Schedule> {

  private List<CarrierMovement> carrierMovements = Collections.EMPTY_LIST;

  public static final Schedule EMPTY = new Schedule();

  public Schedule(List<CarrierMovement> carrierMovements) {
    Validate.notNull(carrierMovements);
    Validate.noNullElements(carrierMovements);
      
    this.carrierMovements = carrierMovements;
  }

  /**
   * @return Carrier movements.
   */
  public List<CarrierMovement> carrierMovements() {
    return Collections.unmodifiableList(carrierMovements);
  }

  @Override
  public boolean sameValueAs(Schedule other) {
    return other != null && this.carrierMovements.equals(other.carrierMovements);
  }

  @Override
  public Schedule copy() {
    final List<CarrierMovement> copyCarrierMovements = new ArrayList(carrierMovements.size());
    for (CarrierMovement carrierMovement : carrierMovements) {
      copyCarrierMovements.add(carrierMovement.copy());
    }

    return new Schedule(copyCarrierMovements);
  }

  @Override
  public boolean equals(Object o) {
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
