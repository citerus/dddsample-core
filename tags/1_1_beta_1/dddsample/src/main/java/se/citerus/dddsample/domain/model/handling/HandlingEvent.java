package se.citerus.dddsample.domain.model.handling;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import se.citerus.dddsample.domain.model.DomainEvent;
import se.citerus.dddsample.domain.model.ValueObject;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.DomainObjectUtils;

import java.util.Comparator;
import java.util.Date;

/**
 * A HandlingEvent is used to register the event when, for instance,
 * a cargo is unloaded from a carrier at a some loacation at a given time.
 * <p/>
 * The HandlingEvent's are sent from different Incident Logging Applications
 * some time after the event occured and contain information about the
 * {@link se.citerus.dddsample.domain.model.cargo.TrackingId}, {@link se.citerus.dddsample.domain.model.location.Location}, timestamp of the completion of the event,
 * and possibly, if applicable a {@link se.citerus.dddsample.domain.model.carrier.Voyage}.
 * <p/>
 * This class is the only member, and consequently the root, of the HandlingEvent aggregate. 
 * <p/>
 * HandlingEvent's could contain information about a {@link Voyage} and if so,
 * the event type must be either {@link Type#LOAD} or {@link Type#UNLOAD}.
 * <p/>
 * All other events must be of {@link Type#RECEIVE}, {@link Type#CLAIM} or {@link Type#CUSTOMS}.
 */
public final class HandlingEvent implements DomainEvent<HandlingEvent> {

  /**
   * Comparator used to be able to sort HandlingEvents according to their completion time
   */
  public static final Comparator<HandlingEvent> BY_COMPLETION_TIME_COMPARATOR =
    new Comparator<HandlingEvent>() {
      public int compare(final HandlingEvent he1, final HandlingEvent he2) {
        return he1.completionTime().compareTo(he2.completionTime());
      }
    };

  private Type type;
  private Voyage voyage;
  private Location location;
  private Date completionTime;
  private Date registrationTime;
  private Cargo cargo;

  /**
   * Handling event type. Either requires or prohibits a carrier movement
   * association, it's never optional.
   */
  public enum Type implements ValueObject<Type> {
    LOAD(true),
    UNLOAD(true),
    RECEIVE(false),
    CLAIM(false),
    CUSTOMS(false);

    private final boolean voyageRequired;

    /**
     * Private enum constructor.
     *
     * @param voyageRequired whether or not a voyage is associated with this event type
     */
    private Type(final boolean voyageRequired) {
      this.voyageRequired = voyageRequired;
    }

    /**
     * @return True if a voyage association is required for this event type.
     */
    public boolean requiresVoyage() {
      return voyageRequired;
    }

    /**
     * @return True if a voyage association is prohibited for this event type.
     */
    public boolean prohibitsVoyage() {
      return !requiresVoyage();
    }

    @Override
    public boolean sameValueAs(Type other) {
      return other != null && this.equals(other);
    }

  }

  /**
   * @param cargo            cargo
   * @param completionTime   completion time, the reported time that the event actually happened (e.g. the receive took place).
   * @param registrationTime registration time, the time the message is received
   * @param type             type of event
   * @param location         where the event took place
   * @param voyage           the voyage
   */
  public HandlingEvent(final Cargo cargo,
                       final Date completionTime,
                       final Date registrationTime,
                       final Type type,
                       final Location location,
                       final Voyage voyage) {
    Validate.noNullElements(new Object[]
      {cargo, completionTime, registrationTime, type, location, voyage}
    );

    if (type.prohibitsVoyage()) {
      throw new IllegalArgumentException("Voyage is not allowed with event type " + type);
    }

    this.voyage = voyage;
    this.completionTime = (Date) completionTime.clone();
    this.registrationTime = (Date) registrationTime.clone();
    this.type = type;
    this.location = location;
    this.cargo = cargo;
  }

  /**
   * @param cargo            cargo
   * @param completionTime   completion time, the reported time that the event actually happened (e.g. the receive took place).
   * @param registrationTime registration time, the time the message is received
   * @param type             type of event
   * @param location         where the event took place
   */
  public HandlingEvent(final Cargo cargo,
                       final Date completionTime,
                       final Date registrationTime,
                       final Type type,
                       final Location location) {
    Validate.noNullElements(new Object[]
      {cargo, completionTime, registrationTime, type, location}
    );

    if (type.requiresVoyage()) {
      throw new IllegalArgumentException("Voyage is required for event type " + type);
    }

    this.completionTime = (Date) completionTime.clone();
    this.registrationTime = (Date) registrationTime.clone();
    this.type = type;
    this.location = location;
    this.cargo = cargo;
    this.voyage = null;
  }

  public Type type() {
    return this.type;
  }

  public Voyage voyage() {
    return DomainObjectUtils.nullSafe(this.voyage, Voyage.NONE);
  }

  public Date completionTime() {
    return new Date(this.completionTime.getTime());
  }

  public Date registrationTime() {
    return new Date(this.registrationTime.getTime());
  }

  public Location location() {
    return this.location;
  }

  public Cargo cargo() {
    return this.cargo;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final HandlingEvent event = (HandlingEvent) o;

    return sameEventAs(event);
  }

  @Override
  public boolean sameEventAs(final HandlingEvent other) {
    return other != null && new EqualsBuilder().
      append(this.cargo, other.cargo).
      append(this.voyage, other.voyage).
      append(this.completionTime, other.completionTime).
      append(this.location, other.location).
      append(this.type, other.type).
      isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(cargo).
      append(voyage).
      append(completionTime).
      append(location).
      append(type).
      toHashCode();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  HandlingEvent() {
    // Needed by Hibernate
  }


  // Auto-generated surrogate key
  private Long id;

}
