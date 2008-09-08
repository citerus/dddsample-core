package se.citerus.dddsample.ui;

import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.support.RequestContextUtils;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.service.TrackingService;
import se.citerus.dddsample.ui.command.TrackCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for tracking cargo. This interface sits immediately on top of the
 * domain layer, unlike the booking interface which has a a remote facade and supporting
 * DTOs in between.
 * <p/>
 * An adapter class, designed for the tracking use case, is used to wrap the domain model
 * to make it easier to work with in a web page rendering context. We do not want to apply
 * view rendering constraints to the design of our domain model, and the adapter
 * helps us shield the domain model classes. 
 * <p/>
 *
 * @eee se.citerus.dddsample.application.web.CargoTrackingViewAdapter
 * @see CargoAdminController
 *
 */
public final class CargoTrackingController extends SimpleFormController {

  private TrackingService trackingService;

  public CargoTrackingController() {
    setCommandClass(TrackCommand.class);
  }

  @Override
  protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object command, final BindException errors) throws Exception {

    final TrackCommand trackCommand = (TrackCommand) command;
    final String trackingIdString = trackCommand.getTrackingId();
    final Cargo cargo = trackingService.track(new TrackingId(trackingIdString));

    final Map<String, CargoTrackingViewAdapter> model = new HashMap();
    if (cargo != null) {
      final MessageSource messageSource = getApplicationContext();
      final Locale locale = RequestContextUtils.getLocale(request);
      model.put("cargo", new CargoTrackingViewAdapter(cargo, messageSource, locale));
    } else {
      errors.rejectValue("trackingId", "cargo.unknown_id", new Object[]{trackCommand.getTrackingId()},
        "Unknown tracking id");
    }
    return showForm(request, response, errors, model);
  }

  public void setTrackingService(TrackingService trackingService) {
    this.trackingService = trackingService;
  }
}
