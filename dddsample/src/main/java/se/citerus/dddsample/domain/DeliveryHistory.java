package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A wrapper class that holds a sorted set of HandlingEvents. The set can not contain events with the same timestamp.
 * 
 */
@Entity
@Table(name = "delivery_history")
public class DeliveryHistory {

  @Id
  private Long id;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "delivery_history_fk")
  @Sort(type = SortType.NATURAL)
  private final SortedSet<HandlingEvent> events = new TreeSet<HandlingEvent>();

  public SortedSet<HandlingEvent> getEvents() {
    return events;
  }

  /**
   * Adds the HandlingEvent to the sorted set.
   * 
   * @throws IllegalArgumentException if an event is not unique. Uniquness are evaluated by checking that compareTo() not returns 0.
   * @param event
   */
  public void addEvent(HandlingEvent event) {
    if (!events.add(event)){
      throw new IllegalArgumentException("HandlingEvent are not evaluated to be unique");
    }
  }

  public HandlingEvent last() {
    return events.isEmpty() ? null : events.last();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
