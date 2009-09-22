package se.citerus.dddsample.tracking.core.domain.model.handling;

import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;

public class UnknownLocationException extends CannotCreateHandlingEventException {

  private final UnLocode unlocode;

  public UnknownLocationException(final UnLocode unlocode) {
    this.unlocode = unlocode;
  }

  @Override
  public String getMessage() {
    return "No location with UN locode " + unlocode.stringValue() + " exists in the system";
  }
}
