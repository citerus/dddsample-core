package se.citerus.dddsample.web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.service.CargoService;
import se.citerus.dddsample.web.command.TrackCommand;

import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for tracking cargo.
 */
public class CargoTrackingController extends SimpleFormController {

  /**
   * Service instance.
   */
  private CargoService cargoService;

  public CargoTrackingController() {
    setCommandClass(TrackCommand.class);
  }

  @Override
  public ModelAndView onSubmit(final Object command) throws ServletException {
    final Map<String, Object> model = new HashMap<String, Object>();

    final TrackCommand trackCommand = (TrackCommand) command;
    logger.debug("Finding cargo by trackingId: " + trackCommand.getTrackingId());
    final Cargo cargo = cargoService.find(trackCommand.getTrackingId());

    if (cargo != null) {
      final Location location = cargo.currentLocation();
      logger.debug("Location of [" + trackCommand.getTrackingId() + "] is [" + location + "]");
      model.put("location", location);
    }
    return new ModelAndView(getSuccessView(), model);
  }

  /**
   * Sets the cargo service instance.
   *
   * @param cargoService The service.
   */
  public void setCargoService(final CargoService cargoService) {
    this.cargoService = cargoService;
  }
}
