package se.citerus.dddsample.interfaces.booking.web;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(legs).hasSize(2);

        RouteAssignmentCommand.LegCommand leg = legs.get(0);
        assertThat(leg.getVoyageNumber()).isEqualTo("CM01");
        assertThat(leg.getFromUnLocode()).isEqualTo("AAAAA");
        assertThat(leg.getToUnLocode()).isEqualTo("BBBBB");

        leg = legs.get(1);
        assertThat(leg.getVoyageNumber()).isEqualTo("CM02");
        assertThat(leg.getFromUnLocode()).isEqualTo("CCCCC");
        assertThat(leg.getToUnLocode()).isEqualTo("DDDDD");

        assertThat(command.getTrackingId()).isEqualTo("XYZ");
    }
}