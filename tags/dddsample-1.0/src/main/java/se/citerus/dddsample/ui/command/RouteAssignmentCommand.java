package se.citerus.dddsample.ui.command;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;

import java.util.ArrayList;
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
    private String carrierMovementId;
    private String fromUnLocode;
    private String toUnLocode;

    public String getCarrierMovementId() {
      return carrierMovementId;
    }

    public void setCarrierMovementId(final String carrierMovementId) {
      this.carrierMovementId = carrierMovementId;
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

    public static Factory factory() {
      return new Factory() {
        public Object create() {
          return new LegCommand();
        }
      };
    }
    
  }
}
