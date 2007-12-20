package se.citerus.dddsample.web.command;

public class TrackCommand {

  /**
   * The tracking id.
   */
  private String trackingId;

  public String getTrackingId() {
    return trackingId;
  }

  public void setTrackingId(final String trackingId) {
    this.trackingId = trackingId;
  }

  @Override
  public String toString() {
    return "TrackCommand{" +
        "trackingId='" + trackingId + '\'' +
        '}';
  }
}
