package se.citerus.dddsample.interfaces.booking.web;

import org.springframework.context.MessageSource;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;

import java.util.Locale;

public class CargoAdminViewAdapter {
    private final MessageSource messageSource;
    private final Locale locale;

    public CargoAdminViewAdapter(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

    public String getSelectItinerarySummaryText(CargoRoutingDTO cargo) {
        return messageSource.getMessage("cargo.admin.itinerary.summary", new Object[]{cargo.getTrackingId(), cargo.getOrigin(), cargo.getFinalDestination()}, locale);
    }

    public String getRouteCandidateCaption(Integer index) {
        return messageSource.getMessage("cargo.admin.itinerary.routecandidatecaption", new Object[]{index}, locale);
    }
}
