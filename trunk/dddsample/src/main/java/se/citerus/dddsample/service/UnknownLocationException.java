package se.citerus.dddsample.service;

public class UnknownLocationException extends Exception {

  private String unlocode;

  public UnknownLocationException(String unlocode) {
    this.unlocode = unlocode;
  }


  @Override
  public String getMessage() {
    return "No location with UN location code " + unlocode + " exists in the system";
  }
}
