package se.citerus.dddsample.interfaces.tracking;

import org.springframework.context.MessageSource;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Delivery;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * View adapter for displaying a cargo in a tracking context.
 */
public final class CargoTrackingViewAdapter {

  private final Cargo cargo;
  private final MessageSource messageSource;
  private final Locale locale;
  private final List<HandlingEventViewAdapter> events;

  /**
   * Constructor.
   *
   * @param cargo
   * @param messageSource
   * @param locale
   * @param handlingEvents
   */
  public CargoTrackingViewAdapter(Cargo cargo, MessageSource messageSource, Locale locale, List<HandlingEvent> handlingEvents) {
    this.messageSource = messageSource;
    this.locale = locale;
    this.cargo = cargo;

    this.events = new ArrayList<HandlingEventViewAdapter>(handlingEvents.size());
    for (HandlingEvent handlingEvent : handlingEvents) {
      events.add(new HandlingEventViewAdapter(handlingEvent));
    }
  }

  /**
   * @param location a location
   * @return A formatted string for displaying the location.
   */
  private String getDisplayText(Location location) {
    return location.unLocode().idString() + " (" + location.name() + ")";
  }

  /**
   * @return An unmodifiable list of handling event view adapters.
   */
  public List<HandlingEventViewAdapter> getEvents() {
    return Collections.unmodifiableList(events);
  }

  /**
   * @return A translated string describing the cargo status. 
   */
  public String getStatusText() {
    final Delivery delivery = cargo.delivery();
    final String code = "cargo.status." + delivery.transportStatus().name();

    final Object[] args;
    switch (delivery.transportStatus()) {
      case IN_PORT:
        args = new Object[] {getDisplayText(delivery.lastKnownLocation())};
        break;
      case ONBOARD_CARRIER:
        args = new Object[] {delivery.currentVoyage().voyageNumber().idString()};
        break;
      case CLAIMED:
      case NOT_RECEIVED:
      case UNKNOWN:
      default:
        args = null;
        break;
    }
    
    return messageSource.getMessage(code, args, "[Unknown status]", locale);
  }

  /**
   * @return Cargo destination location.
   */
  public String getDestination() {
    return getDisplayText(cargo.routeSpecification().destination());
  }

  /**
   * @return Cargo osigin location.
   */
  public String getOrigin() {
    return getDisplayText(cargo.origin());
  }

  /**
   * @return Cargo tracking id.
   */
  public String getTrackingId() {
    return cargo.trackingId().idString();
  }

  /**
   * @return True if cargo is misdirected.
   */
  public boolean isMisdirected() {
    return cargo.isMisdirected();
  }

  /**
   * Handling event view adapter component.
   */
  public final class HandlingEventViewAdapter {

    private final HandlingEvent handlingEvent;
    private final String FORMAT = "yyyy-MM-dd hh:mm";

    /**
     * Constructor.
     *
     * @param handlingEvent handling event
     */
    public HandlingEventViewAdapter(HandlingEvent handlingEvent) {
      this.handlingEvent = handlingEvent;
    }

    /**
     * @return Location where the event occurred.
     */
    public String getLocation() {
      return handlingEvent.location().unLocode().idString();
    }

    /**
     * @return Time when the event was completed.
     */
    public String getTime() {
      return new SimpleDateFormat(FORMAT).format(handlingEvent.completionTime());
    }

    /**
     * @return Type of event.
     */
    public String getType() {
      return handlingEvent.type().toString();
    }

    /**
     * @return Voyage number, or empty string if not applicable.
     */
    public String getVoyageNumber() {
      final Voyage voyage = handlingEvent.voyage();
      return voyage.voyageNumber().idString();
    }

    /**
     * @return True if the event was expected, according to the cargo's itinerary.
     */
    public boolean isExpected() {
      return cargo.itinerary().isExpected(handlingEvent);
    }

  }
}
