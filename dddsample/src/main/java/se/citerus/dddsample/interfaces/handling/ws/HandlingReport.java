package se.citerus.dddsample.interfaces.handling.ws;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Data type for registering handling events
 * using the web service api. Focus here is interoperability
 * and maintaining a stable api, and possibly things like
 * backwards compatibility with a system that's being replaced,
 * or adhering to an industry standard.
 *
 * We do not want this to constrain our modeling in any way.
 * 
 */
public class HandlingReport {

  @XmlElement(required = true)
  private String[] trackingIds;

  @XmlElement(required = true)
  private String unLocode;

  @XmlElement(required = false)
  private String voyageNumber;

  @XmlSchemaType(name="dateTime")
  @XmlElement(required = true)
  private XMLGregorianCalendar completionTime;

  @XmlElement(required = true)
  private String type;

  /**
   * @return tracking ids of cargos that have been handled (the same way)
   */
  public String[] getTrackingIds() {
    return trackingIds;
  }

  public void setTrackingIds(String[] trackingIds) {
    this.trackingIds = trackingIds;
  }

  /**
   * @return United Nations Location Code for the location where the event occured
   */
  public String getUnLocode() {
    return unLocode;
  }

  public void setUnLocode(String unLocode) {
    this.unLocode = unLocode;
  }

  /**
   * Not all events are associated with a voyage (customs handling etc).
   * 
   * @return voyage number, if applicable
   */
  public String getVoyageNumber() {
    return voyageNumber;
  }

  public void setVoyageNumber(String voyageNumber) {
    this.voyageNumber = voyageNumber;
  }

  /**
   * @return time when event occured, for example a the loading of cargo was completed
   */
  public XMLGregorianCalendar getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(XMLGregorianCalendar completionTime) {
    this.completionTime = completionTime;
  }

  /**
   * @return type of event
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
