package se.citerus.dddsample.interfaces.tracking;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public final class TrackCommand {

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
    return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
  }
}
