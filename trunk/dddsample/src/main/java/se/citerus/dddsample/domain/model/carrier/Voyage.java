package se.citerus.dddsample.domain.model.carrier;

import se.citerus.dddsample.domain.model.Entity;

/**
 * A Voyage.
 */
public class Voyage implements Entity<Voyage> {

  private VoyageNumber voyageNumber;
  private Schedule schedule;

  // Null object pattern
  public static final Voyage NONE = new Voyage(
    new VoyageNumber(""), Schedule.EMPTY
  );

  public Voyage(VoyageNumber voyageNumber, Schedule schedule) {
    this.voyageNumber = voyageNumber;
    this.schedule = schedule;
  }

  /**
   * @return Voyage number.
   */
  public VoyageNumber voyageNumber() {
    return voyageNumber;
  }

  /**
   * @return Schedule.
   */
  public Schedule schedule() {
    return schedule;
  }

  @Override
  public int hashCode() {
    return voyageNumber.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (!(o instanceof Voyage)) return false;

    final Voyage that = (Voyage) o;

    return sameIdentityAs(that);
  }

  @Override
  public boolean sameIdentityAs(Voyage other) {
    return other != null && this.voyageNumber().sameValueAs(other.voyageNumber());
  }

  Voyage() {
    // Needed by Hibernate
  }

  // Needed by Hibernate
  private Long id;

}
