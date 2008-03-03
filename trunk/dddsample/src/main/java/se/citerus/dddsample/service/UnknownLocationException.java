package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.UnLocode;

public class UnknownLocationException extends Exception {

  private UnLocode unlocode;

  public UnknownLocationException(UnLocode unlocode) {
    this.unlocode = unlocode;
  }

  @Override
  public String getMessage() {
    return "No location with UN locode " + unlocode.idString() + " exists in the system";
  }
}
