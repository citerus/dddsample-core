package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Embeddable;
import java.util.Arrays;
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

  // Country code is exactly two letters
  private static final Pattern countryCodePattern = Pattern.compile("[a-zA-Z]{2}");

  // Location code is usually three letters, but may contain the numbers 2-9 as well
  private static final Pattern locationCodePattern = Pattern.compile("[a-zA-Z2-9]{3}");

  public UnLocode(String countryCode, String locationCode) {
    validateArgs(countryCode, locationCode);

    this.unlocode = (countryCode + locationCode).toUpperCase();
  }

  private void validateArgs(String countryCode, String locationCode) {
    Validate.noNullElements(new Object[] {countryCode, locationCode},
            "Neither country code nor location code may be null");
    Validate.isTrue(countryCodePattern.matcher(countryCode).matches(),
      "\"" + countryCode + "\" is not a valid country code");
    Validate.isTrue(locationCodePattern.matcher(locationCode).matches(),
      "\"" + locationCode + "\" is not a valid location code");
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
