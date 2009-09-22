package se.citerus.dddsample.tracking.core.domain.shared.experimental;

/**
 * Base class for entities.
 */
public abstract class EntitySupport<T extends Entity, ID> implements Entity<T, ID> {

  @Override
  public final boolean sameAs(final T other) {
    return other != null && this.identity().equals(other.identity());
  }

  @Override
  public abstract ID identity();

  @Override
  public final int hashCode() {
    return identity().hashCode();
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    return sameAs((T) o);
  }

}
