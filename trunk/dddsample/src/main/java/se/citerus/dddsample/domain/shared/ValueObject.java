package se.citerus.dddsample.domain.shared;

import java.io.Serializable;

/**
 * A value object, as described in the DDD book.
 * 
 */
public interface ValueObject<T> extends Serializable {

  /**
   * Value objects compare by the values of their attributes, they don't have an identity.
   *
   * @param other The other value object.
   * @return <code>true</code> if the given value object's and this value object's attributes are the same.
   */
  boolean sameValueAs(T other);

}
