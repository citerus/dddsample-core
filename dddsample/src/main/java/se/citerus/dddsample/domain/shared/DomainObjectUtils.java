package se.citerus.dddsample.domain.shared;

/**
 * Utility code for domain classes. 
 *
 */
public class DomainObjectUtils {
  public static <T> T nullSafe(T actual, T safe) {
    return actual == null ? safe : actual;
  }
}
