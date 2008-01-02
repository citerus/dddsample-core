package se.citerus.dddsample.web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

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
  private String unknownCargoView;

  public CargoTrackingController() {
    setCommandClass(TrackCommand.class);
    setBindOnNewForm(true);
  }

  @Override
  public ModelAndView onSubmit(final Object command) throws ServletException {
    ModelAndView mav = null;
    final Map<String, Object> model = new HashMap<String, Object>();

    final TrackCommand trackCommand = (TrackCommand) command;
    logger.debug("Finding cargo by trackingId: " + trackCommand.getTrackingId());
    final Cargo cargo = cargoService.find(trackCommand.getTrackingId());

    if (cargo != null) {
      final Location location = cargo.currentLocation();
      logger.debug("Location of [" + trackCommand.getTrackingId() + "] is [" + location + "]");
      model.put("location", location);
      mav = new ModelAndView(getSuccessView(), model);
    } else {
      model.put("trackingId", trackCommand.getTrackingId());
      mav = new ModelAndView(getUnknownCargoView(), model);
    }
    
    return mav;
  }

  /**
   * Sets the cargo service instance.
   *
   * @param cargoService The service.
   */
  public void setCargoService(final CargoService cargoService) {
    this.cargoService = cargoService;
  }
  
  /**
   * Sets the view to show when an unknown tracking id is submitted.
   *
   * @param unknownCargoView The view.
   */
  public void setUnknownCargoView(final String unkownCargoView) {
    this.unknownCargoView = unkownCargoView;
  }

  /**
   * Gets the view to show when an unknown tracking id is submitted
   * 
   * @return The View
   */
  public String getUnknownCargoView() {
    return unknownCargoView;
  }
  
  
}
