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
import se.citerus.dddsample.domain.*;
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
      public CargoWithHistoryDTO track(String trackingId) {
        final Location a5 = new Location(new UnLocode("AA","AAA"), "AAAAA");
        final Location b5 = new Location(new UnLocode("BB","BBB"), "BBBBB");
        Cargo cargo = new Cargo(new TrackingId(trackingId), a5, b5);
        HandlingEvent event = new HandlingEvent(cargo, new Date(10L), new Date(20L), HandlingEvent.Type.RECEIVE, b5);
//        cargo.handle(event);

        // TODO: use DTO assemblers
        CargoWithHistoryDTO cargoDTO = new CargoWithHistoryDTO(
                cargo.trackingId().idString(),
                cargo.origin().unLocode().idString(),
                cargo.finalDestination().unLocode().idString(),
                StatusCode.claimed,
                "AAAAA",
                "BALO");
        cargoDTO.addEvent(new HandlingEventDTO(
          event.location().unLocode().idString(),
          event.type().toString(),
          null, // TODO: event hierarchy will remove this kind of code
          event.completionTime()));
        return cargoDTO;
      }
    };
  }
  
  private CargoService getCargoServiceNullMock() {
    return new CargoService() {
      public CargoWithHistoryDTO track(String trackingId) {
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
    request.addParameter("trackingId","JKL456");
    request.setMethod("POST");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    // Errors, command are two standard map attributes, the third should be the cargo object
    assertEquals(3, mav.getModel().size());
    CargoWithHistoryDTO cargo = (CargoWithHistoryDTO) mav.getModel().get("cargo");
    assertEquals("AAAAA", cargo.getCurrentLocationId());
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
