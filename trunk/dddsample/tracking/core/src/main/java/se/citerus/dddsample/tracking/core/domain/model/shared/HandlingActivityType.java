package se.citerus.dddsample.tracking.core.domain.model.shared;

import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObject;

/**
 * Handling activity type. May or may not be voyage related and may or may not be physical.
 *
 */
public enum HandlingActivityType implements ValueObject<HandlingActivityType> {

  LOAD(true, true),
  UNLOAD(true, true),
  RECEIVE(false, true),
  CLAIM(false, true),
  CUSTOMS(false, false);

  private final boolean voyageRelated;
  private final boolean physical;

  /**
   * Private enum constructor.
   *
   * @param voyageRelated whether or not a voyage is associated with this event type
   * @param physical whether or not this event type is physical
   */
  private HandlingActivityType(final boolean voyageRelated, final boolean physical) {
    this.voyageRelated = voyageRelated;
    this.physical = physical;
  }

  /**
   * @return True if a voyage association is required for this event type.
   */
  public boolean isVoyageRelated() {
    return voyageRelated;
  }

  /**
   * @return True if this is a physical handling.
   */
  public boolean isPhysical() {
    return physical;
  }

  @Override
  public boolean sameValueAs(final HandlingActivityType other) {
    return this.equals(other);
  }

}
