package se.citerus.dddsample.domain.model.location;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.shared.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A geographical zone within which there are no customs restrictions or checks.
 */
public class CustomsZone implements Entity<CustomsZone> {

  private String code;
  private String name;

  // TODO: Find out what the standards are for this, if any. For now:
  // For CustomsZone code, we are using the "country code" portion of the UnLocode,
  // except within economic areas that are not countries, such as the EU. Then we make one up.
  // Country code is exactly two letters, so we'll use 2 letters.
  private static final Pattern VALID_PATTERN = Pattern.compile("[a-zA-Z]{2}");

  /**
   * Constructor.
   *
   * @param code code
   * @param name name
   */
  public CustomsZone(final String code, final String name) {
    Validate.notNull(code, "Code is required");
    Validate.isTrue(VALID_PATTERN.matcher(code).matches(),
      code + " is not a valid CustomsZone code (does not match pattern)");
    Validate.notNull(name, "Name is required");
    this.code = code.toUpperCase();
    this.name = name;
  }

  /**
   * Where would a Cargo enter this CustomsZone if it were
   * following this route. Note that specific voyages, etc do not
   * matter, only the sequence of Locations.
   *
   * @param route
   * @return
   */
  public Location entryPoint(List<Location> route) {
    for (Location location : route) {
      if (this.includes(location)) return location;
    }
    return null; //The route does not enter this CustomsZone
  }

  /**
   * Convenience method for testing. Usage in application would be
   * entryPoint(List<Location) route)
   *
   * @param route
   * @return
   */
  public Location entryPoint(Location... route) {
    return entryPoint(Arrays.asList(route));
  }

  /**
   * If the rules of the CustomsZone were different or more complex,
   * this is where that rule would be expressed. For the example, we'll
   * just use the basic rule -- customs clearance is at the entry point.
   *
   * @param route
   * @return
   */
//   public Location clearancePoint(List<Location> route) {
//     return entryPoint(route);
//   }

  /**
   * Convenience method for testing. Usage in application would be
   * clearancePoint(List<Location) route)
   *
   * @param route
   * @return
   */
//   @SuppressWarnings("unchecked")
//   public Location clearancePoint(Location ... route) {
//     return clearancePoint(Arrays.asList(route));
//   }
//


  /**
   * @return Code, always upper case.
   */
  public String code() {
    return code;
  }

  /**
   * @return The name.
   */
  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return code + "[" + name + "]";
  }

  @Override
  public boolean sameIdentityAs(CustomsZone other) {
    return code.equals(other.code);
  }

  CustomsZone() {
    // Needed by Hibernate
  }

  public boolean includes(Location location) {
    return this.sameIdentityAs(location.customsZone());
  }


}
