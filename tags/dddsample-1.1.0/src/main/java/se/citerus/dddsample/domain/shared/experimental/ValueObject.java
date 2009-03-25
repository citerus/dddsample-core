package se.citerus.dddsample.domain.shared.experimental;

/**
 * A value object.
 *
 */
public interface ValueObject<T> {

  /**
   * Value objects compare by the values of their attributes, they don't have an identity.
   *
   * @param other The other value object.
   * @return <code>true</code> if the given value object's and this value object's attributes are the same.
   */
  boolean sameValueAs(T other);

  /**
   * Value objects can be freely copied.
   *
   * @return A safe, deep copy of this value object.
   */
  T copy();

}