package se.citerus.dddsample.interfaces.tracking.ws;

import org.springframework.context.MessageSource;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Delivery;
import se.citerus.dddsample.domain.model.cargo.HandlingActivity;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;
import se.citerus.dddsample.interfaces.booking.facade.internal.assembler.CargoRoutingDTOAssembler;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CargoTrackingDTOConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withZone(ZoneOffset.UTC);

    public static CargoTrackingDTO convert(Cargo cargo, List<HandlingEvent> handlingEvents, MessageSource messageSource, Locale locale) {
        List<HandlingEventDTO> handlingEventDTOs = convertHandlingEvents(handlingEvents, cargo, messageSource, locale);
        return new CargoTrackingDTO(
                convertTrackingId(cargo),
                convertStatusText(cargo, messageSource, locale),
                convertOrigin(cargo),
                convertDestination(cargo),
                convertEta(cargo),
                convertNextExpectedActivity(cargo),
                convertIsMisdirected(cargo),
                convertLegs(cargo),
                handlingEventDTOs);
    }

    private static List<CargoLegDTO> convertLegs(Cargo cargo) {
        if(cargo.itinerary == null) {
            return Collections.emptyList();
        }
        return cargo.itinerary.stream()
                .map(CargoTrackingDTOConverter::convertLeg)
                .collect(Collectors.toList());
    }

    private static CargoLegDTO convertLeg(Leg leg) {
        return new CargoLegDTO(
                leg.voyage().voyageNumber().idString(),
                leg.loadLocation().unLocode().idString(),
                leg.unloadLocation().unLocode().idString(),
                leg.loadTime().toString(),
                leg.unloadTime().toString());
    }

    private static List<HandlingEventDTO> convertHandlingEvents(List<HandlingEvent> handlingEvents, Cargo cargo, MessageSource messageSource, Locale locale) {

        return handlingEvents.stream().map(he -> new HandlingEventDTO(
                convertLocation(he),
                convertTime(he),
                convertType(he),
                convertVoyageNumber(he),
                convertIsExpected(he, cargo),
                convertDescription(he, messageSource, locale)
        )).collect(Collectors.toList());
    }

    protected static String convertDescription(HandlingEvent handlingEvent, MessageSource messageSource, Locale locale) {
        Object[] args;

        switch (handlingEvent.type()) {
            case LOAD:
            case UNLOAD:
                args = new Object[]{
                        handlingEvent.voyage().voyageNumber().idString(),
                        handlingEvent.location().name(),
                        formatter.format(handlingEvent.completionTime())
                };
                break;
            case RECEIVE:
            case CUSTOMS:
            case CLAIM:
                args = new Object[]{
                        handlingEvent.location().name(),
                        formatter.format(handlingEvent.completionTime())
                };
                break;

            default:
                args = new Object[]{};
        }

        String key = "deliveryHistory.eventDescription." + handlingEvent.type().name();

        return messageSource.getMessage(key, args, locale);
    }

    private static boolean convertIsExpected(HandlingEvent handlingEvent, Cargo cargo) {
        return cargo.itinerary().isExpected(handlingEvent);
    }

    private static String convertVoyageNumber(HandlingEvent handlingEvent) {
        return handlingEvent.voyage().voyageNumber().idString();
    }

    private static String convertType(HandlingEvent handlingEvent) {
        return handlingEvent.type().toString();
    }

    private static String convertTime(HandlingEvent handlingEvent) {
        return formatter
                .format(handlingEvent.completionTime());
    }

    private static String convertLocation(HandlingEvent handlingEvent) {
        return handlingEvent.location().name();
    }

    private static String convertTrackingId(Cargo cargo) {
        return cargo.trackingId().idString();
    }

    protected static String convertStatusText(Cargo cargo, MessageSource messageSource, Locale locale) {
        final Delivery delivery = cargo.delivery();
        final String code = "cargo.status." + delivery.transportStatus().name();

        final Object[] args;
        switch (delivery.transportStatus()) {
            case IN_PORT:
                args = new Object[]{delivery.lastKnownLocation().name()};
                break;
            case ONBOARD_CARRIER:
                args = new Object[]{delivery.currentVoyage().voyageNumber().idString()};
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

    private static LocationDTO convertOrigin(Cargo cargo) {
        Location loc = cargo.routeSpecification().origin();
        return new LocationDTO(loc.unlocode, loc.name);
    }

    private static LocationDTO convertDestination(Cargo cargo) {
        Location loc = cargo.routeSpecification().destination();
        return new LocationDTO(loc.unlocode, loc.name);
    }

    private static String convertEta(Cargo cargo) {
        Instant date = cargo.delivery().estimatedTimeOfArrival();
        return date == null ? "Unknown" : date.toString();
    }

    protected static String convertNextExpectedActivity(Cargo cargo) {
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

    private static boolean convertIsMisdirected(Cargo cargo) {
        return cargo.delivery().isMisdirected();
    }
}
