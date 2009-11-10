package se.citerus.dddsample.reporting.api;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Handling {

  private String type;
  private String location;
  private String voyage;
  private String completedOn;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getVoyage() {
    return voyage;
  }

  public void setVoyage(String voyage) {
    this.voyage = voyage;
  }

  public String getCompletedOn() {
    return completedOn;
  }

  public void setCompletedOn(String completedOn) {
    this.completedOn = completedOn;
  }

  @Override
  public boolean equals(Object that) {
    return EqualsBuilder.reflectionEquals(this, that);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
