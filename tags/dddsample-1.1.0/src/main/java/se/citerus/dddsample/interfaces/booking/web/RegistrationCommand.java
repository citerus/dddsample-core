package se.citerus.dddsample.interfaces.booking.web;

/**
 *
 */
public final class RegistrationCommand {

  private String originUnlocode;
  private String destinationUnlocode;
  private String arrivalDeadline;

  public String getOriginUnlocode() {
    return originUnlocode;
  }

  public void setOriginUnlocode(final String originUnlocode) {
    this.originUnlocode = originUnlocode;
  }

  public String getDestinationUnlocode() {
    return destinationUnlocode;
  }

  public void setDestinationUnlocode(final String destinationUnlocode) {
    this.destinationUnlocode = destinationUnlocode;
  }

  public String getArrivalDeadline() {
    return arrivalDeadline;
  }

  public void setArrivalDeadline(String arrivalDeadline) {
    this.arrivalDeadline = arrivalDeadline;
  }
}
