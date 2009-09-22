package se.citerus.dddsample.tracking.core.domain.shared.experimental;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every class that inherits from {@link se.citerus.dddsample.tracking.core.domain.shared.experimental.EntitySupport}
 * must have exactly one field annotated with this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Identity {
}
