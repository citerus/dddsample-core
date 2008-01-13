package se.citerus.dddsample.web;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.service.CargoService;
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;
import se.citerus.dddsample.web.command.TrackCommand;

import java.util.Date;

public class CargoTrackingControllerTest extends TestCase {
  CargoTrackingController controller;
  MockHttpServletRequest request;
  MockHttpServletResponse response;
  MockHttpSession session;
  MockServletContext servletContext;

  protected void setUp() throws Exception {
    servletContext = new MockServletContext("test");
    request = new MockHttpServletRequest(servletContext);
    response = new MockHttpServletResponse();
    session = new MockHttpSession(servletContext);
    request.setSession(session);
    
    controller = new CargoTrackingController();
    controller.setFormView("test-form");
    controller.setSuccessView("test-success");
    controller.setCommandName("test-command-name");
  }

  private CargoService getCargoServiceMock() {
    return new CargoService() {
      public CargoWithHistoryDTO find(String trackingId) {
        Cargo cargo = new Cargo(new TrackingId(trackingId), new Location("AAA"), new Location("BBB"));
        HandlingEvent event = new HandlingEvent(new Date(10L), new Date(20L), HandlingEvent.Type.RECEIVE);
        cargo.getDeliveryHistory().addEvent(event);

        // TODO: use DTO assemblers
        CargoWithHistoryDTO cargoDTO = new CargoWithHistoryDTO(
                cargo.trackingId().getId(),
                cargo.origin().unlocode(),
                cargo.finalDestination().unlocode(),
                cargo.getCurrentLocation().unlocode()
        );
        cargoDTO.addEvent(new HandlingEventDTO(
          event.getLocation().unlocode(),
          event.getType().toString(),
          null, // TODO: event hierarchy will remove this kind of code
          event.getTimeOccurred()));
        return cargoDTO;
      }
    };
  }
  
  private CargoService getCargoServiceNullMock() {
    return new CargoService() {
      public CargoWithHistoryDTO find(String trackingId) {
        return null;
      }
    };
  }

  public void testHandleGet() throws Exception {
    controller.setCargoService(getCargoServiceMock());
    request.setMethod("GET");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    assertEquals(2, mav.getModel().size());
    assertTrue(mav.getModel().get("test-command-name") instanceof TrackCommand);
  }

  public void testHandlePost() throws Exception {
    controller.setCargoService(getCargoServiceMock());
    request.setMethod("POST");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    // Errors, command are two standard map attributes, the third should be the cargo object
    assertEquals(3, mav.getModel().size());
    CargoWithHistoryDTO cargo = (CargoWithHistoryDTO) mav.getModel().get("cargo");
    assertEquals("AAA", cargo.getCurrentLocation());
  }

  public void testUnknownCargo() throws Exception {
    controller.setCargoService(getCargoServiceNullMock());
    request.setMethod("POST");
    request.setParameter("trackingId", "unknown-id");
    
    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    assertEquals(2, mav.getModel().size());

    TrackCommand command = (TrackCommand) mav.getModel().get(controller.getCommandName());
    assertEquals("unknown-id", command.getTrackingId());

    Errors errors = (Errors) mav.getModel().get(BindingResult.MODEL_KEY_PREFIX + controller.getCommandName());
    FieldError fe = errors.getFieldError("trackingId");
    assertEquals("cargo.unknown_id", fe.getCode());
    assertEquals(1, fe.getArguments().length);
    assertEquals(command.getTrackingId(), fe.getArguments()[0]);
  }

}
