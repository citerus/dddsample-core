package se.citerus.dddsample.domain.shared.experimental;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.ValueObject;

public class ValueObjectSupport<T extends ValueObject> implements ValueObject<T> {

    private transient int cachedHashCode = 0;

    @Override
    public final boolean sameValueAs(final T other) {
        return other != null && EqualsBuilder.reflectionEquals(this, other, false);
    }

    @Override
    public final int hashCode() {
        // Using a local variable to ensure that we only do a single read
        // of the cachedHashCode field, to avoid race conditions.
        // It doesn't matter if several threads compute the hash code and overwrite
        // each other, but it's important that we never return 0, which could happen
        // with multiple field reads.
        int h = cachedHashCode;
        if (h == 0) {
            // Lazy initialization of hash code.
            // Value objects are immutable, so the hash code never changes.
            h = HashCodeBuilder.reflectionHashCode(this, false);
            cachedHashCode = h;
        }

        return h;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return sameValueAs((T) o);
    }

}