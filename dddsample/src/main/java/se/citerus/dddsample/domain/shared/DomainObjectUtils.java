package se.citerus.dddsample.domain.shared;

/**
 * Utility code for domain classes. 
 *
 */
public class DomainObjectUtils {

  /**
   * @param actual actual value
   * @param safe a null-safe value
   * @param <T> type
   * @return actual value, if it's not null, or safe value if the actual value is null.
   */
  public static <T> T nullSafe(T actual, T safe) {
    return actual == null ? safe : actual;
  }

  // TODO wrappers for some of the commons-lang code:
  //
  // EqualsBuilder that uses sameIdentity/sameValue,
  // better validation (varargs etc) 

  /**
   * Prevent instantiation.
   */
  private DomainObjectUtils() {
  }

}
