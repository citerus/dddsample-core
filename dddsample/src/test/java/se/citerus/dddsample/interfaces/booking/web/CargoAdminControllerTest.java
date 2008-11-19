package se.citerus.dddsample.interfaces.booking.web;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade;

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
    request = new MockHttpServletRequest("GET","assignItinerary.html");
    response = new MockHttpServletResponse();

    controller.handleRequest(request, response);
  }

}
