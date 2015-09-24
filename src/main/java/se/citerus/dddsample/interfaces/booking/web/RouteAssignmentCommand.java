package se.citerus.dddsample.interfaces.booking.web;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RouteAssignmentCommand {

  private String trackingId;
  private List<LegCommand> legs = ListUtils.lazyList(
    new ArrayList(), LegCommand.factory()
  );

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
    private Date fromDate;
    private Date toDate;

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

    public Date getFromDate() {
      return fromDate;
    }

    public void setFromDate(Date fromDate) {
      this.fromDate = fromDate;
    }

    public Date getToDate() {
      return toDate;
    }

    public void setToDate(Date toDate) {
      this.toDate = toDate;
    }

    public static Factory factory() {
      return LegCommand::new;
    }

  }
}
