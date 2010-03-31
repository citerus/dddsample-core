package se.citerus.dddsample.tracking.bookingui.web;

public class VoyageDelayCommand {

  private DelayType type;
  private String voyageNumber;
  private int hours;

  public DelayType getType() {
    return type;
  }

  public String getVoyageNumber() {
    return voyageNumber;
  }

  public int getHours() {
    return hours;
  }

  public static enum DelayType {
    DEPT, ARR
  }
}
