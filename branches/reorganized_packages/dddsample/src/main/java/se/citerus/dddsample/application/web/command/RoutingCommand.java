package se.citerus.dddsample.application.web.command;

import java.util.ArrayList;
import java.util.List;

public final class RoutingCommand {

  private List<ItineraryCandidateCommand> itineraryCandidates = new ArrayList<ItineraryCandidateCommand>();

  public List<ItineraryCandidateCommand> getItineraryCandidates() {
    return itineraryCandidates;
  }

  public void setItineraryCandidates(final List<ItineraryCandidateCommand> itineraryCandidates) {
    this.itineraryCandidates = itineraryCandidates;
  }

  public static final class ItineraryCandidateCommand {
    private String trackingId;
    private List<LegCommand> legs = new ArrayList<LegCommand>();

    public String getTrackingId() {
      return trackingId;
    }

    public void setTrackingId(final String trackingId) {
      this.trackingId = trackingId;
    }

    public List<LegCommand> getLegs() {
      return legs;
    }

    public void setLegs(final List<LegCommand> legs) {
      this.legs = legs;
    }
  }

  public static final class LegCommand {
    private String carrierMovementId;
    private String fromUnlocode;
    private String toUnlocode;

    public String getCarrierMovementId() {
      return carrierMovementId;
    }

    public void setCarrierMovementId(final String carrierMovementId) {
      this.carrierMovementId = carrierMovementId;
    }

    public String getFromUnlocode() {
      return fromUnlocode;
    }

    public void setFromUnlocode(final String fromUnlocode) {
      this.fromUnlocode = fromUnlocode;
    }

    public String getToUnlocode() {
      return toUnlocode;
    }

    public void setToUnlocode(final String toUnlocode) {
      this.toUnlocode = toUnlocode;
    }
  }
}
