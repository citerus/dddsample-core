package se.citerus.dddsample.interfaces.booking.web;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RouteAssignmentCommand {

  private String trackingId;
  private List<LegCommand> legs = new ArrayList<>();

  public String getTrackingId() {
    return trackingId;
  }

  public void setTrackingId(String trackingId) {
    this.trackingId = trackingId;
  }

  public List<LegCommand> getLegs() {
    return legs;
  }

  public void setLegs(List<LegCommand> legs) {
    this.legs = legs;
  }

  public static final class LegCommand {
    private String voyageNumber;
    private String fromUnLocode;
    private String toUnLocode;
    private Instant fromDate;
    private Instant toDate;

    public String getVoyageNumber() {
      return voyageNumber;
    }

    public void setVoyageNumber(final String voyageNumber) {
      this.voyageNumber = voyageNumber;
    }

    public String getFromUnLocode() {
      return fromUnLocode;
    }

    public void setFromUnLocode(final String fromUnLocode) {
      this.fromUnLocode = fromUnLocode;
    }

    public String getToUnLocode() {
      return toUnLocode;
    }

    public void setToUnLocode(final String toUnLocode) {
      this.toUnLocode = toUnLocode;
    }

    public Instant getFromDate() {
      return fromDate;
    }

    public void setFromDate(String fromDate) {
      this.fromDate = Instant.parse(fromDate);
    }

    public Instant getToDate() {
      return toDate;
    }

    public void setToDate(String toDate) {
      this.toDate = Instant.parse(toDate);
    }
  }
}
