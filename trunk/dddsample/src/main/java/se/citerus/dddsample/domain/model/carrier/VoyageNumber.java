package se.citerus.dddsample.domain.model.carrier;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.ValueObject;

/**
 * Identifies a voyage.
 * 
 */
public class VoyageNumber implements ValueObject<VoyageNumber> {

  private String number;

  public VoyageNumber(String number) {
    Validate.notNull(number);
    
    this.number = number;
  }

  @Override
  public boolean sameValueAs(VoyageNumber other) {
    return other != null && this.number.equals(other.number);
  }

  @Override
  public VoyageNumber copy() {
    return new VoyageNumber(number);
  }

  public String idString() {
    return number;
  }
  
}
