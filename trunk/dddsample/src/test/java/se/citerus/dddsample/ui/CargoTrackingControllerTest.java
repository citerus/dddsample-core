package se.citerus.dddsample.ui;

import junit.framework.TestCase;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoTestHelper;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.TOKYO;
import se.citerus.dddsample.domain.service.TrackingService;
import se.citerus.dddsample.ui.command.TrackCommand;

import java.util.Arrays;
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
    StaticApplicationContext applicationContext = new StaticApplicationContext();
    controller.setApplicationContext(applicationContext);
    controller.setFormView("test-form");
    controller.setSuccessView("test-success");
    controller.setCommandName("test-command-name");
  }

  private TrackingService getCargoServiceMock() {
    return new EmptyStubTrackingService() {

      public Cargo track(TrackingId trackingId) {
        final Cargo cargo = new Cargo(trackingId, HONGKONG, TOKYO);
        final HandlingEvent event = new HandlingEvent(cargo, new Date(10L), new Date(20L), HandlingEvent.Type.RECEIVE, HONGKONG);
        CargoTestHelper.setDeliveryHistory(cargo, Arrays.asList(event));
        
        return cargo;
      }
    };
  }

  private TrackingService getTrackingServiceNullMock() {
    return new EmptyStubTrackingService();
  }

  public void testHandleGet() throws Exception {
    controller.setTrackingService(getCargoServiceMock());
    request.setMethod("GET");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    assertEquals(2, mav.getModel().size());
    assertTrue(mav.getModel().get("test-command-name") instanceof TrackCommand);
  }

  public void testHandlePost() throws Exception {
    controller.setTrackingService(getCargoServiceMock());
    request.addParameter("trackingId", "JKL456");
    request.setMethod("POST");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    // Errors, command are two standard map attributes, the third should be the cargo object
    assertEquals(3, mav.getModel().size());
    CargoTrackingViewAdapter cargo = (CargoTrackingViewAdapter) mav.getModel().get("cargo");
    assertEquals("JKL456", cargo.getTrackingId());
  }

  public void testUnknownCargo() throws Exception {
    controller.setTrackingService(getTrackingServiceNullMock());
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

  private class EmptyStubTrackingService implements TrackingService {
    public Cargo track(TrackingId trackingId) {
      return null;
    }
    public void inspectCargo(TrackingId trackingId) {
    }
  }
}
