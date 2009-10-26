package se.citerus.dddsample.tracking.core.domain.model.handling;

/**
 * If a {@link se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent} can't be
 * created from a given set of parameters.
 * <p/>
 * It is a checked exception because it's not a programming error, but rather a
 * special case that the application is built to handle. It can occur during normal
 * program execution.
 */
public class CannotCreateHandlingEventException extends RuntimeException {
  public CannotCreateHandlingEventException(Exception e) {
    super(e);
  }

  public CannotCreateHandlingEventException() {
    super();
  }
}
