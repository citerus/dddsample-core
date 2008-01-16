package se.citerus.dddsample.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


/**
 * A Cargo an entity identifed by TrackingId and is capable of getting its DeliveryHistory plus a number
 * of convenience operation for finding current destination etc.
 */
@Entity
public class Cargo {

  @EmbeddedId
  private TrackingId trackingId;

  @ManyToOne
  private Location origin;
  
  @ManyToOne
  private Location finalDestination;
  
  @OneToMany
  private final Set<HandlingEvent> events = new HashSet<HandlingEvent>();


  public Cargo(TrackingId trackingId, Location origin, Location finalDestination) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
  }

//  
//  public DeliveryHistory deliveryHistory() {
//    return history;
//  }

  public void handle(HandlingEvent event) {
    events.add(event);
  }

  
  /**
   * @return An <b>unmodifiable</b> list of handling events, ordered by the time the events occured.
   */
  public List<HandlingEvent> eventsOrderedByTime() {
    List<HandlingEvent> eventList = new ArrayList<HandlingEvent>(events);
    Collections.sort(eventList, HandlingEvent.BY_TIMESTAMP_COMPARATOR);
    return Collections.unmodifiableList(eventList);
  }

  /**
   * 
   * @return The last handled event
   */
  public HandlingEvent lastEvent() {
    if (events.isEmpty()) {
      return null;
    } else {
      List<HandlingEvent> orderedEvents = eventsOrderedByTime();
      return orderedEvents.get(orderedEvents.size() - 1);
    }
  }
  
  /**
   * Checks if the Cargo's last event was reported at the same Location as the final destination. 
   * 
   * Note that this doesn't nessecary mean that the Cargo has been delivered. Possibly there are more handling to be done before the Cargo can be claimed at the final destination
   * 
   * @return true if Cargos is at final destination otherwise false.
   */
  public boolean atFinalDestiation() {
    return currentLocation().equals(finalDestination);
  }

  /**
   * Returns the last known Location or Location.UNKOWN if no HandlingEvent history can be found for this Cargo
   * 
   * TODO: Rename this to lastKnownLocation. 
   * 
   * @return The last known location
   */
  public Location currentLocation() {
    HandlingEvent lastEvent = lastEvent();
    
    // If we have no last event, we have not even received the package. Return unknown location
    if (lastEvent == null) {
      return Location.UNKNOWN;
    }
   
    Location location = lastEvent.location();
      
    return location;
  }

  public TrackingId trackingId() {
    return trackingId;
  }

  public Location origin() {
    return origin;
  }

  public Location finalDestination() {
    return finalDestination;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Cargo)) {
      return false;
    }
    Cargo rhs = (Cargo) obj;
    return new EqualsBuilder()
      .append(trackingId, rhs.trackingId)
      .append(origin, rhs.origin)
      .append(finalDestination, rhs.finalDestination)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(7, 39)
    .append(trackingId)
    .append(origin)
    .append(finalDestination)
    .toHashCode();
  }
  
  // Needed by Hibernate
  Cargo() {}

  
}
