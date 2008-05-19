package se.citerus.dddsample.web.command;

import java.util.ArrayList;
import java.util.List;

public class RoutingCommand {
  List<ItineraryCandidateCommand> itineraryCandidates = new ArrayList<ItineraryCandidateCommand>();

  public List<ItineraryCandidateCommand> getItineraryCandidates() {
    return itineraryCandidates;
  }

  public void setItineraryCandidates(List<ItineraryCandidateCommand> itineraryCandidates) {
    this.itineraryCandidates = itineraryCandidates;
  }

  public static class ItineraryCandidateCommand {
    String trackingId;
    List<LegCommand> legs = new ArrayList<LegCommand>();

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
  }

  public static class LegCommand {
    String carrierMovementId;
    String fromUnlocode;
    String toUnlocode;

    public String getCarrierMovementId() {
      return carrierMovementId;
    }

    public void setCarrierMovementId(String carrierMovementId) {
      this.carrierMovementId = carrierMovementId;
    }

    public String getFromUnlocode() {
      return fromUnlocode;
    }

    public void setFromUnlocode(String fromUnlocode) {
      this.fromUnlocode = fromUnlocode;
    }

    public String getToUnlocode() {
      return toUnlocode;
    }

    public void setToUnlocode(String toUnlocode) {
      this.toUnlocode = toUnlocode;
    }
  }
}
