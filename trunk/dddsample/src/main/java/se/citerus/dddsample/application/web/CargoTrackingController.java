package se.citerus.dddsample.application.web;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import se.citerus.dddsample.application.remoting.dto.CargoTrackingDTO;
import se.citerus.dddsample.application.remoting.dto.assembler.CargoTrackingDTOAssembler;
import se.citerus.dddsample.application.web.command.TrackCommand;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.service.TrackingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for tracking cargo.
 */
public final class CargoTrackingController extends SimpleFormController {

  /**
   * Service instance.
   */
  private TrackingService trackingService;

  public CargoTrackingController() {
    setCommandClass(TrackCommand.class);
  }

  @Override
  protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object command, final BindException errors) throws Exception {

    final TrackCommand trackCommand = (TrackCommand) command;
    final String tidStr = trackCommand.getTrackingId();
    final Cargo cargo = trackingService.track(new TrackingId(tidStr));

    final Map<String, CargoTrackingDTO> model = new HashMap<String, CargoTrackingDTO>();
    if (cargo != null) {
      final CargoTrackingDTO dto = new CargoTrackingDTOAssembler().toDTO(cargo);
      model.put("cargo", dto);
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
