package se.citerus.dddsample.interfaces.tracking;

import org.springframework.context.MessageSource;
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

import javax.servlet.http.HttpServletRequest;
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
 * @eee se.citerus.dddsample.application.web.CargoTrackingViewAdapter
 * @see se.citerus.dddsample.interfaces.booking.web.CargoAdminController
 */
@Controller
@RequestMapping("/track")
public final class CargoTrackingController {

    private CargoRepository cargoRepository;
    private HandlingEventRepository handlingEventRepository;
    private MessageSource messageSource;

    @RequestMapping(method = RequestMethod.GET)
    public String get(final Map<String, Object> model) {
        model.put("trackCommand", new TrackCommand());
        return "track";
    }

    @RequestMapping(method = RequestMethod.POST)
    protected String onSubmit(final HttpServletRequest request,
                                                             final TrackCommand command,
                                                             final Map<String, Object> model,
                                                             final BindingResult bindingResult) {
        new TrackCommandValidator().validate(command, bindingResult);

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

    public void setCargoRepository(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
        this.handlingEventRepository = handlingEventRepository;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
