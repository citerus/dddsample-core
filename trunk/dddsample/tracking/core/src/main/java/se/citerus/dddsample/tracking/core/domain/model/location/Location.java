package se.citerus.dddsample.tracking.core.domain.model.location;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.patterns.entity.EntitySupport;

import java.util.TimeZone;

/**
 * A location is our model is stops on a journey, such as cargo
 * origin or destination, or carrier movement endpoints.
 * <p/>
 * It is uniquely identified by a UN Locode.
 */
public class Location extends EntitySupport<Location, UnLocode> {

  private final UnLocode unLocode;
  private final String name;
  private TimeZone timeZone;
  private CustomsZone customsZone;

  /**
   * Special Location object that marks an unknown location.
   */
  public static final Location NONE = new Location(
    new UnLocode("XXXXX"), "-", TimeZone.getTimeZone("Zulu"), null
  );

  /**
   * Package-level constructor, visible for test only.
   *
   * @param unLocode    UN Locode
   * @param name        location name
   * @param timeZone    time zone
   * @param customsZone customs zone
   * @throws IllegalArgumentException if the UN Locode or name is null
   */
  Location(final UnLocode unLocode, final String name, final TimeZone timeZone, final CustomsZone customsZone) {
    Validate.notNull(unLocode);
    Validate.notNull(name);
    Validate.notNull(timeZone);
//    Validate.notNull(customsZone);

    this.unLocode = unLocode;
    this.name = name;
    this.timeZone = timeZone;
    this.customsZone = customsZone;
  }

  @Override
  public UnLocode identity() {
    return unLocode;
  }

  /**
   * @return UN Locode for this location.
   */
  public UnLocode unLocode() {
    return unLocode;
  }

  /**
   * @return Actual name of this location, e.g. "Stockholm".
   */
  public String name() {
    return name;
  }

  /**
   * @return Customs zone of this location.
   */
  public CustomsZone customsZone() {
    return customsZone;
  }

  /**
   * @return Time zone of this location.
   */
  public TimeZone timeZone() {
    return timeZone;
  }

  @Override
  public String toString() {
    return name + " [" + unLocode + "]";
  }

  Location() {
    // Needed by Hibernate
    unLocode = null;
    name = null;
  }

}
