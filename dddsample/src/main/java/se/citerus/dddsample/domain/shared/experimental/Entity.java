package se.citerus.dddsample.domain.shared.experimental;

/**
 * An entity, as explained in the DDD book.
 *
 */
public interface Entity<T,ID> {

  /**
   * Entities compare by identity, not by attributes.
   *
   * @param other The other entity.
   * @return true if the identities are the same, regardles of other attributes.
   */
  boolean sameIdentityAs(T other);

  /**
   * Entities have an identity.
   *
   * @return The identity of this entity.
   */
  ID identity();

}