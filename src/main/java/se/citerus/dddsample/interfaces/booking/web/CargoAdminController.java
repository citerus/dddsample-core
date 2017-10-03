package se.citerus.dddsample.interfaces.booking.web;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Handles cargo booking and routing. Operates against a dedicated remoting service facade,
 * and could easily be rewritten as a thick Swing client. Completely separated from the domain layer,
 * unlike the tracking user interface.
 * <p>
 * In order to successfully keep the domain model shielded from user interface considerations,
 * this approach is generally preferred to the one taken in the tracking controller. However,
 * there is never any one perfect solution for all situations, so we've chosen to demonstrate
 * two polarized ways to build user interfaces.
 *
 * @see se.citerus.dddsample.interfaces.tracking.CargoTrackingController
 */
@Controller
@RequestMapping("/admin")
public final class CargoAdminController {

    private BookingServiceFacade bookingServiceFacade;

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), false));
    }

    @RequestMapping("/registration")
    public String registration(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        List<LocationDTO> dtoList = bookingServiceFacade.listShippingLocations();

        List<String> unLocodeStrings = new ArrayList<String>();

        for (LocationDTO dto : dtoList) {
            unLocodeStrings.add(dto.getUnLocode());
        }

        model.put("unlocodes", unLocodeStrings);
        model.put("locations", dtoList);
        return "admin/registrationForm";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(HttpServletRequest request, HttpServletResponse response,
                         RegistrationCommand command) throws Exception {
        Date arrivalDeadline = new SimpleDateFormat("dd/MM/yyyy").parse(command.getArrivalDeadline());
        String trackingId = bookingServiceFacade.bookNewCargo(
                command.getOriginUnlocode(), command.getDestinationUnlocode(), arrivalDeadline
        );
        response.sendRedirect("show?trackingId=" + trackingId);
    }

    @RequestMapping("/list")
    public String list(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        List<CargoRoutingDTO> cargoList = bookingServiceFacade.listAllCargos();

        model.put("cargoList", cargoList);
        return "admin/list";
    }

    @RequestMapping("/show")
    public String show(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        String trackingId = request.getParameter("trackingId");
        CargoRoutingDTO dto = bookingServiceFacade.loadCargoForRouting(trackingId);
        model.put("cargo", dto);
        return "admin/show";
    }

    @RequestMapping("/selectItinerary")
    public String selectItinerary(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        String trackingId = request.getParameter("trackingId");

        List<RouteCandidateDTO> routeCandidates = bookingServiceFacade.requestPossibleRoutesForCargo(trackingId);
        model.put("routeCandidates", routeCandidates);

        CargoRoutingDTO cargoDTO = bookingServiceFacade.loadCargoForRouting(trackingId);
        model.put("cargo", cargoDTO);

        return "admin/selectItinerary";
    }

    @RequestMapping(value = "/assignItinerary", method = RequestMethod.POST)
    public void assignItinerary(HttpServletRequest request, HttpServletResponse response, RouteAssignmentCommand command) throws Exception {
        List<LegDTO> legDTOs = new ArrayList<LegDTO>(command.getLegs().size());
        for (RouteAssignmentCommand.LegCommand leg : command.getLegs()) {
            legDTOs.add(new LegDTO(
                            leg.getVoyageNumber(),
                            leg.getFromUnLocode(),
                            leg.getToUnLocode(),
                            leg.getFromDate(),
                            leg.getToDate())
            );
        }

        RouteCandidateDTO selectedRoute = new RouteCandidateDTO(legDTOs);

        bookingServiceFacade.assignCargoToRoute(command.getTrackingId(), selectedRoute);

        response.sendRedirect("show.html?trackingId=" + command.getTrackingId());
        //response.sendRedirect("list.html");
    }

    @RequestMapping(value = "/pickNewDestination")
    public String pickNewDestination(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        List<LocationDTO> locations = bookingServiceFacade.listShippingLocations();
        model.put("locations", locations);

        String trackingId = request.getParameter("trackingId");
        CargoRoutingDTO cargo = bookingServiceFacade.loadCargoForRouting(trackingId);
        model.put("cargo", cargo);

        return "admin/pickNewDestination";
    }

    @RequestMapping(value = "/changeDestination", method = RequestMethod.POST)
    public void changeDestination(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String trackingId = request.getParameter("trackingId");
        String unLocode = request.getParameter("unlocode");
        bookingServiceFacade.changeDestination(trackingId, unLocode);
        response.sendRedirect("show.html?trackingId=" + trackingId);
    }

    public void setBookingServiceFacade(BookingServiceFacade bookingServiceFacade) {
        this.bookingServiceFacade = bookingServiceFacade;
    }
}
