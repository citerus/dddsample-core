package se.citerus.dddsample.tracking.core.domain.model.location;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A geographical zone within which there are no customs restrictions or checks.
 */
public class CustomsZone extends ValueObjectSupport<CustomsZone> {

  private final String code;
  private final String name;

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
  CustomsZone(final String code, final String name) {
    Validate.notNull(code, "Code is required");
    Validate.isTrue(VALID_PATTERN.matcher(code).matches(), code + " is not a valid customs zone code (does not match pattern)");
    Validate.notNull(name, "Name is required");

    this.code = code.toUpperCase();
    this.name = name;
  }
  
  /**
   * Where would a Cargo enter this CustomsZone if it were
   * following this route. Note that specific voyages, etc do not
   * matter, only the sequence of Locations.
   *
   * @param route a list of locations
   * @return The first location on the route that is in this customs zone.
   */
  public Location entryPoint(final List<Location> route) {
    for (Location location : route) {
      if (this.includes(location)) {
        return location;
      }
    }
    return null; //The route does not enter this CustomsZone
  }

  /**
   * Convenience method for testing. Usage in application would be
   * entryPoint(List<Location>) route)
   *
   * @param route a list of locations
   * @return The first location on the route that is in this customs zone.
   */
  public Location entryPoint(final Location... route) {
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

  boolean includes(final Location location) {
    return this.sameValueAs(location.customsZone());
  }

  @Override
  public String toString() {
    return code + "[" + name + "]";
  }

  CustomsZone() {
    // Needed by Hibernate
    code = name = null;
  }

}
