package se.citerus.dddsample.interfaces.booking.web;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ItinerarySelectionCommandTest {

    RouteAssignmentCommand command;
    MockHttpServletRequest request;

    @Test
    public void testBind() {
        command = new RouteAssignmentCommand();
        request = new MockHttpServletRequest();

        request.addParameter("legs[0].voyageNumber", "CM01");
        request.addParameter("legs[0].fromUnLocode", "AAAAA");
        request.addParameter("legs[0].toUnLocode", "BBBBB");

        request.addParameter("legs[1].voyageNumber", "CM02");
        request.addParameter("legs[1].fromUnLocode", "CCCCC");
        request.addParameter("legs[1].toUnLocode", "DDDDD");

        request.addParameter("trackingId", "XYZ");

        ServletRequestDataBinder binder = new ServletRequestDataBinder(command);
        binder.bind(request);

        List<RouteAssignmentCommand.LegCommand> legs = command.getLegs();
        assertEquals(2, legs.size());

        RouteAssignmentCommand.LegCommand leg = legs.get(0);
        assertEquals("CM01", leg.getVoyageNumber());
        assertEquals("AAAAA", leg.getFromUnLocode());
        assertEquals("BBBBB", leg.getToUnLocode());

        leg = legs.get(1);
        assertEquals("CM02", leg.getVoyageNumber());
        assertEquals("CCCCC", leg.getFromUnLocode());
        assertEquals("DDDDD", leg.getToUnLocode());

        assertEquals("XYZ", command.getTrackingId());
    }
}