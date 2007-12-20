package se.citerus.dddsample.web;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.service.CargoService;

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
    controller.setCargoService(new CargoService() {
      public Cargo find(String trackingId) {
        Cargo cargo = new Cargo(
          new TrackingId(trackingId),
          new Location("AAA"),
          new Location("BBB"));
        return cargo;
      }
    });
  }

  public void testHandleGet() throws Exception {
    request.setMethod("GET");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    assertEquals(2, mav.getModel().size());
    assertTrue(mav.getModel().get("test-command-name") instanceof TrackCommand);
  }

  public void testHandlePost() throws Exception {
    request.setMethod("POST");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-success", mav.getViewName());
    assertEquals(1, mav.getModel().size());
    Location location = (Location) mav.getModel().get("location");
    assertEquals("AAA", location.unlocode());
  }

}
