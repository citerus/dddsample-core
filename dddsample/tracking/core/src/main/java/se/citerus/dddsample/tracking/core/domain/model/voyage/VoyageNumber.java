package se.citerus.dddsample.tracking.core.domain.model.voyage;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.shared.experimental.ValueObjectSupport;

/**
 * Identifies a voyage.
 */
public class VoyageNumber extends ValueObjectSupport<VoyageNumber> {

  private final String number;

  public VoyageNumber(final String number) {
    Validate.notNull(number);

    this.number = number;
  }

  @Override
  public String toString() {
    return number;
  }

  public String stringValue() {
    return number;
  }

  VoyageNumber() {
    // Needed by Hibernate
    number = null;
  }

}
