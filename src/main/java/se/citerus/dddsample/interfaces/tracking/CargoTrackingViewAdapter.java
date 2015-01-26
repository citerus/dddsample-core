package se.citerus.dddsample.interfaces.tracking;

import org.springframework.context.MessageSource;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Delivery;
import se.citerus.dddsample.domain.model.cargo.HandlingActivity;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * View adapter for displaying a cargo in a tracking context.
 */
public final class CargoTrackingViewAdapter {

  private final Cargo cargo;
  private final MessageSource messageSource;
  private final Locale locale;
  private final List<HandlingEventViewAdapter> events;
  private final String FORMAT = "yyyy-MM-dd hh:mm";
  private final TimeZone timeZone;

    /**
   * Constructor.
   *
   * @param cargo
   * @param messageSource
   * @param locale
   * @param handlingEvents
   */
  public CargoTrackingViewAdapter(Cargo cargo, MessageSource messageSource, Locale locale, List<HandlingEvent> handlingEvents) {
    this(cargo, messageSource, locale, handlingEvents, TimeZone.getDefault());
  }

    /**
     * Constructor.
     *
     * @param cargo
     * @param messageSource
     * @param locale
     * @param handlingEvents
     */
    public CargoTrackingViewAdapter(Cargo cargo, MessageSource messageSource, Locale locale, List<HandlingEvent> handlingEvents, TimeZone tz) {
      this.messageSource = messageSource;
      this.locale = locale;
      this.cargo = cargo;
      this.timeZone = tz;

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
    return location.name();
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

  public String getEta() {
    Date eta = cargo.delivery().estimatedTimeOfArrival();

    if (eta == null) return "?";
    else return new SimpleDateFormat(FORMAT).format(eta);
  }

  public String getNextExpectedActivity() {
      HandlingActivity activity = cargo.delivery().nextExpectedActivity();
      if (activity == null) {
        return "";
      }

    String text = "Next expected activity is to ";
    HandlingEvent.Type type = activity.type();
    if (type.sameValueAs(HandlingEvent.Type.LOAD)) {
        return
          text + type.name().toLowerCase() + " cargo onto voyage " + activity.voyage().voyageNumber() +
          " in " + activity.location().name();
      } else if (type.sameValueAs(HandlingEvent.Type.UNLOAD)) {
        return
          text + type.name().toLowerCase() + " cargo off of " + activity.voyage().voyageNumber() +
          " in " + activity.location().name();
      } else {
        return text + type.name().toLowerCase() + " cargo in " + activity.location().name();
      }
  }

  /**
   * @return True if cargo is misdirected.
   */
  public boolean isMisdirected() {
    return cargo.delivery().isMisdirected();
  }

  /**
   * Handling event view adapter component.
   */
  public final class HandlingEventViewAdapter {

    private final HandlingEvent handlingEvent;

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
      return handlingEvent.location().name();
    }

    /**
     * @return Time when the event was completed.
     */
    public String getTime() {
      final SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
      sdf.setTimeZone(timeZone);
      return sdf.format(handlingEvent.completionTime());
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

    public String getDescription() {
      Object[] args;

      switch (handlingEvent.type()) {
        case LOAD:
        case UNLOAD:
          args = new Object[] {
            handlingEvent.voyage().voyageNumber().idString(),
            handlingEvent.location().name(),
            handlingEvent.completionTime()
          };
          break;

        case RECEIVE:
        case CLAIM:
          args = new Object[] {
            handlingEvent.location().name(),
            handlingEvent.completionTime()
          };
          break;

        default:
          args = new Object[] {};
      }

      String key = "deliveryHistory.eventDescription." + handlingEvent.type().name();

      return messageSource.getMessage(key,args,locale);
    }

  }
  
}
