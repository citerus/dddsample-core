package se.citerus.dddsample.interfaces.tracking;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.infrastructure.persistence.inmemory.CargoRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.HandlingEventRepositoryInMem;

public class CargoTrackingControllerTest extends TestCase {
  CargoTrackingController controller;
  MockHttpServletRequest request;
  MockHttpServletResponse response;
  MockHttpSession session;
  MockServletContext servletContext;
  CargoRepositoryInMem cargoRepository;
  HandlingEventRepository handlingEventRepository;

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
    cargoRepository = new CargoRepositoryInMem();
    cargoRepository.setHandlingEventRepository(new HandlingEventRepositoryInMem());
    cargoRepository.init();

    handlingEventRepository = new HandlingEventRepositoryInMem();
    controller.setHandlingEventRepository(handlingEventRepository);
  }

  public void testHandleGet() throws Exception {
    controller.setCargoRepository(new CargoRepositoryInMem());
    request.setMethod("GET");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    assertEquals(2, mav.getModel().size());
    assertTrue(mav.getModel().get("test-command-name") instanceof TrackCommand);
  }

  public void testHandlePost() throws Exception {
    controller.setCargoRepository(cargoRepository);
    request.addParameter("trackingId", "ABC");
    request.setMethod("POST");

    ModelAndView mav = controller.handleRequest(request, response);

    assertEquals("test-form", mav.getViewName());
    // Errors, command are two standard map attributes, the third should be the cargo object
    assertEquals(3, mav.getModel().size());
    CargoTrackingViewAdapter cargo = (CargoTrackingViewAdapter) mav.getModel().get("cargo");
    assertEquals("ABC", cargo.getTrackingId());
  }

  public void testUnknownCargo() throws Exception {
    CargoRepository cargoRepository = EasyMock.createNiceMock(CargoRepository.class);
    EasyMock.replay(cargoRepository);
    controller.setCargoRepository(cargoRepository);
    
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
