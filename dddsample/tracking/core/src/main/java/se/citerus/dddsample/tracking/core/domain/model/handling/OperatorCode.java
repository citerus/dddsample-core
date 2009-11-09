package se.citerus.dddsample.tracking.core.domain.model.handling;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

/**
 * Port operators are assigned an operator code.
 */
public class OperatorCode extends ValueObjectSupport<OperatorCode> {

  private final String code;

  /**
   * Constructor.
   *
   * @param code code, three letters (ex: "AGH")
   */
  public OperatorCode(final String code) {
    Validate.notEmpty(code, "Code is required");
    Validate.isTrue(code.length() == 5, "Operator codes must be exactly five letters: " + code);
    this.code = code;
  }

  /**
   * @return The operator code as a String
   */
  public String stringValue() {
    return code;
  }

  @Override
  public String toString() {
    return stringValue();
  }

  OperatorCode() {
    // Needed by Hibernate
    code = null;
  }
}
