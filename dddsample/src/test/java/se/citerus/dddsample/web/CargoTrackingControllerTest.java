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
import se.citerus.dddsample.web.command.TrackCommand;

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
    controller.setUnknownCargoView("test-unkownCargo");
    controller.setCommandName("test-command-name");
    controller.setCargoService(getCargoServiceMock());
  }

  private CargoService getCargoServiceMock() {
    return new CargoService() {
      public Cargo find(String trackingId) {
        Cargo cargo = new Cargo(
          new TrackingId(trackingId),
          new Location("AAA"),
          new Location("BBB"));
        return cargo;
      }
    };
  }
  
  private CargoService getCargoServiceNullMock() {
    return new CargoService() {
      public Cargo find(String trackingId) {
        return null;
      }
    };
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
    assertEquals(3, mav.getModel().size());
    Cargo cargo = (Cargo) mav.getModel().get("cargo");
    assertEquals("AAA", cargo.getCurrentLocation().unlocode());
  }

  public void testUnknownCargo() throws Exception {
    controller.setCargoService(getCargoServiceNullMock());
    controller.setCommandClass(TrackCommandMock.class);
    request.setMethod("POST");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-unkownCargo", mav.getViewName());
    assertEquals(1, mav.getModel().size());
    String trackId = (String) mav.getModel().get("trackingId");
    assertEquals("MOCK", trackId);
  }
  
  
  
  
  /**
   * Mock track command.
   * 
   * Sets a default track id when constructed.
   */
  private static class TrackCommandMock extends TrackCommand {
    public TrackCommandMock() {
      setTrackingId("MOCK");
    }
  }
}
