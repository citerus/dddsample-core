package se.citerus.dddsample.domain.shared.experimental;

/**
 * An entity, as explained in the DDD book.
 */
public interface Entity<T, ID> {

  /**
   * Entities have an identity.
   *
   * @return The identity of this entity.
   */
  ID identity();

  /**
   * Entities compare by identity, not by attributes.
   *
   * @param other The other entity.
   * @return true if the identities are the same, regardles of other attributes.
   */
  boolean sameAs(T other);

}