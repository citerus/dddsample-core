package se.citerus.dddsample.tracking.bookingui;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import se.citerus.dddsample.tracking.booking.api.BookingServiceFacade;
import se.citerus.dddsample.tracking.bookingui.web.CargoAdminController;

public class CargoAdminControllerTest extends TestCase {

  CargoAdminController controller;
  BookingServiceFacade bookingServiceFacade;
  MockHttpServletRequest request;
  MockHttpServletResponse response;

  public CargoAdminControllerTest() {
    controller = new CargoAdminController();
    bookingServiceFacade = EasyMock.createMock(BookingServiceFacade.class);
    controller.setBookingServiceFacade(bookingServiceFacade);
  }

  public void testAssignItinerary() throws Exception {
    request = new MockHttpServletRequest("GET", "assignItinerary.html");
    response = new MockHttpServletResponse();

    controller.handleRequest(request, response);
  }

}
