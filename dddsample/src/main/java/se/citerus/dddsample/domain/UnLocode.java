package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

/**
 * United nations location code.
 *
 * http://www.unece.org/cefact/locode/
 * http://www.unece.org/cefact/locode/DocColumnDescription.htm#LOCODE
 */
@Embeddable
public class UnLocode {

  private String unlocode;

  // Country code is exactly two letters.
  // Location code is usually three letters, but may contain the numbers 2-9 as well
  private static final Pattern validPattern = Pattern.compile("[a-zA-Z]{2}[a-zA-Z2-9]{3}");

  public UnLocode(String countryAndLocation) {
    Validate.notNull(countryAndLocation);
    Validate.isTrue(validPattern.matcher(countryAndLocation).matches());

    this.unlocode = countryAndLocation.toUpperCase();
  }

  /**
   * @return country code and location code concatenated
   */
  public String idString() {
    return unlocode;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  UnLocode() {
    // Needed by Hibernate
  }
}
