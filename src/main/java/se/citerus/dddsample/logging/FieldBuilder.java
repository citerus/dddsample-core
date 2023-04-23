package se.citerus.dddsample.logging;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import com.tersesystems.echopraxia.api.Value;
import org.jetbrains.annotations.NotNull;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Delivery;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

import javax.jms.JMSException;
import javax.jms.Message;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

class FieldBuilderInstance {
    static FieldBuilder INSTANCE = new FieldBuilder() {};
}

public interface FieldBuilder extends com.tersesystems.echopraxia.api.FieldBuilder {

    static @NotNull FieldBuilder instance() {
        return FieldBuilderInstance.INSTANCE;
    }

    default Field apply(TrackingId trackingId) {
        return keyValue("trackingId", Value.string(trackingId.idString()));
    }

    default Field keyValue(String name, Location location) {
        return object(name, locationValue(location));
    }

    private Value.ObjectValue locationValue(Location location) {
        return Value.object(
            keyValue("locationId", Value.number(location.id)),
            apply(location.unLocode()),
            keyValue("name", Value.string(location.name()))
        );
    }

    default Field apply(UnLocode unLocode) {
       return keyValue("unLocode", unlocadeValue(unLocode));
    }

    private Value.StringValue unlocadeValue(UnLocode unLocode) {
        return Value.string(unLocode.idString());
    }

    default Field apply(Cargo cargo) {
        return object("cargo", cargoValue(cargo));
    }

    private Value.ObjectValue cargoValue(Cargo cargo) {
        return Value.object(
          keyValue("cargoId", Value.number(cargo.id)),
          apply(cargo.delivery()),
          apply(cargo.trackingId()),
          apply(cargo.routeSpecification())
        );
    }

    default Field apply(RouteSpecification routeSpecification) {
        return object("routeSpecification", routeSpecificationValue(routeSpecification));
    }

    private Value.ObjectValue routeSpecificationValue(RouteSpecification rs) {
        return Value.object(
                keyValue("destination", rs.destination()),
                keyValue("origin", rs.origin()),
                keyValue("arrivalDeadline", instantValue(rs.arrivalDeadline()))
        );
    }

    default Field apply(Delivery delivery) {
        return object("delivery", deliveryValue(delivery));
    }

    private Value.ObjectValue deliveryValue(Delivery delivery) {
        return Value.object(
                keyValue("eta", etaValue(delivery.estimatedTimeOfArrival())),
                keyValue("lastKnownLocation", delivery.lastKnownLocation),
                keyValue("isMisdirected", Value.bool(delivery.isMisdirected())),
                keyValue("calculatedAt", instantValue(delivery.calculatedAt()))
        );
    }

    private Value<String> instantValue(Instant instant) {
        return Value.string(DateTimeFormatter.ISO_INSTANT.format(instant));
    }

    private Value<?> etaValue(Instant instant) {
        if (instant == null) { // "ETA_UNKNOWN" is private
            return Value.nullValue();
        } else {
            return instantValue(instant);
        }
    }

    default Field apply(HandlingEvent event) {
        return keyValue("handlingEvent", handlingEventValue(event));
    }

    private Value.ObjectValue handlingEventValue(HandlingEvent event) {
        return Value.object(
            number("handlingEventId", event.id),
            apply(event.type()),
            keyValue("cargo", cargoValue(event.cargo())),
            keyValue("location", event.location()),
            keyValue("completionTime", instantValue(event.completionTime)),
            keyValue("registrationTime", instantValue(event.registrationTime))
        );
    }

    default FieldBuilderResult apply(HandlingEventRegistrationAttempt attempt) {
        return keyValue("handlingEventRegistrationAttempt", handlingEventRegistrationAttemptValue(attempt));
    }

    default Value.ObjectValue handlingEventRegistrationAttemptValue(HandlingEventRegistrationAttempt attempt) {
        return Value.object(
          keyValue("completionTime", instantValue(attempt.getCompletionTime())) ,
          keyValue("registrationTime", instantValue(attempt.getRegistrationTime())),
          apply(attempt.getTrackingId()),
          apply(attempt.getUnLocode()),
          apply(attempt.getType()),
          apply(attempt.getVoyageNumber())
        );
    }

    default Field apply(VoyageNumber voyageNumber) {
        return keyValue("voyageNumber", Value.string(voyageNumber.idString()));
    }

    default Field apply(HandlingEvent.Type type) {
        return keyValue("handlingEventType", Value.string(type.toString()));
    }

    default Field apply(Message message) {
        return keyValue("jmsMessage", messageValue(message));
    }

    private Value.ObjectValue messageValue(Message message) {
        try {
            return Value.object(
                    keyValue("jmsMessageId", Value.string(message.getJMSMessageID())),
                    keyValue("jmsTimestamp", Value.number(message.getJMSTimestamp())),
                    keyValue("toString", Value.string(message.toString()))
            );
        } catch (JMSException e) {
            return Value.object(
                keyValue("toString", Value.string(message.toString()))
            );
        }
    }
}
