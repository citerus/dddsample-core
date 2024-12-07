package se.citerus.dddsample.interfaces.tracking;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for tracking cargo. This interface sits immediately on top of the
 * domain layer, unlike the booking interface which has a a remote facade and supporting
 * DTOs in between.
 * <p>
 * An adapter class, designed for the tracking use case, is used to wrap the domain model
 * to make it easier to work with in a web page rendering context. We do not want to apply
 * view rendering constraints to the design of our domain model, and the adapter
 * helps us shield the domain model classes.
 * <p>
 *
 * @see CargoTrackingViewAdapter
 * @see se.citerus.dddsample.interfaces.booking.web.CargoAdminController
 */
@Controller
@RequestMapping("/track")
public final class CargoTrackingController {

    private final CargoRepository cargoRepository;
    private final HandlingEventRepository handlingEventRepository;
    private final MessageSource messageSource;
    private final TrackCommandValidator trackCommandValidator;

    public CargoTrackingController(@NonNull CargoRepository cargoRepository,
                                   @NonNull HandlingEventRepository handlingEventRepository,
                                   @NonNull MessageSource messageSource,
                                   @NonNull TrackCommandValidator trackCommandValidator) {
        this.cargoRepository = cargoRepository;
        this.handlingEventRepository = handlingEventRepository;
        this.messageSource = messageSource;
        this.trackCommandValidator = trackCommandValidator;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(final Map<String, Object> model) {
        // using the empty command to support the thymeleaf form `<form method="post" th:object="${trackCommand}">`
        model.put("trackCommand", new TrackCommand());
        return "track";
    }

    @RequestMapping(method = RequestMethod.POST)
    private String onSubmit(final HttpServletRequest request,
                            final TrackCommand command,
                            final Map<String, Object> model,
                            final BindingResult bindingResult) {
        trackCommandValidator.validate(command, bindingResult);

        final TrackingId trackingId = new TrackingId(command.getTrackingId());
        final Cargo cargo = cargoRepository.find(trackingId);

        if (cargo != null) {
            final Locale locale = RequestContextUtils.getLocale(request);
            final List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
            model.put("cargo", new CargoTrackingViewAdapter(cargo, messageSource, locale, handlingEvents));
        } else {
            bindingResult.rejectValue("trackingId", "cargo.unknown_id", new Object[]{command.getTrackingId()}, "Unknown tracking id");
        }
        return "track";
    }
}
