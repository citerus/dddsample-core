package se.citerus.dddsample.interfaces.booking.web;

import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade;

public class CargoAdminControllerTest {

    @Test
    public void testAssignItinerary() throws Exception {
        CargoAdminController cargoAdminController = new CargoAdminController();
        BookingServiceFacade bookingServiceFacade = EasyMock.createMock(BookingServiceFacade.class);
        cargoAdminController.setBookingServiceFacade(bookingServiceFacade);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "assignItinerary.html");
        MockHttpServletResponse response = new MockHttpServletResponse();

        cargoAdminController.handleRequest(request, response);
    }
}