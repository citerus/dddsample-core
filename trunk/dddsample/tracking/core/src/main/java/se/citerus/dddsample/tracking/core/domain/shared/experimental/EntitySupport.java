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
  public final int hashCode() {
    return identity().hashCode();
  }

  @SuppressWarnings({"SimplifiableIfStatement", "unchecked"})
  @Override
  public final boolean equals(final Object o) {
    if (this == o) return true;
    // TODO class comparision is too strict for ORM proxies
    if (o == null || !(o instanceof EntitySupport)) return false;
    //if (o == null || getClass() != o.getClass()) return false;

    return sameAs((T) o);
  }

  @SuppressWarnings("UnusedDeclaration")
  // Surrogate primary key
  private Long id;

}
