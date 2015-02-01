package se.citerus.dddsample.interfaces.booking.web;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handles cargo booking and routing. Operates against a dedicated remoting service facade,
 * and could easily be rewritten as a thick Swing client. Completely separated from the domain layer,
 * unlike the tracking user interface.
 * <p/>
 * In order to successfully keep the domain model shielded from user interface considerations,
 * this approach is generally preferred to the one taken in the tracking controller. However,
 * there is never any one perfect solution for all situations, so we've chosen to demonstrate
 * two polarized ways to build user interfaces.   
 *
 * @see se.citerus.dddsample.interfaces.tracking.CargoTrackingController
 */
public final class CargoAdminController extends MultiActionController {

  private BookingServiceFacade bookingServiceFacade;

  @Override
  protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    super.initBinder(request, binder);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), false));
  }

  public Map registrationForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    List<LocationDTO> dtoList = bookingServiceFacade.listShippingLocations();

    List<String> unLocodeStrings = new ArrayList<String>();

    for (LocationDTO dto : dtoList) {
      unLocodeStrings.add(dto.getUnLocode());
    }

    map.put("unlocodes", unLocodeStrings);
    map.put("locations", dtoList);
    return map;
  }

  public void register(HttpServletRequest request, HttpServletResponse response,
                       RegistrationCommand command) throws Exception {
    Date arrivalDeadline = new SimpleDateFormat("M/dd/yyyy").parse(command.getArrivalDeadline());
    String trackingId = bookingServiceFacade.bookNewCargo(
      command.getOriginUnlocode(), command.getDestinationUnlocode(), arrivalDeadline
    );
    response.sendRedirect("show.html?trackingId=" + trackingId);
  }

  public Map list(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    List<CargoRoutingDTO> cargoList = bookingServiceFacade.listAllCargos();

    map.put("cargoList", cargoList);
    return map;
  }

  public Map show(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    String trackingId = request.getParameter("trackingId");
    CargoRoutingDTO dto = bookingServiceFacade.loadCargoForRouting(trackingId);
    map.put("cargo", dto);
    return map;
  }

  public Map selectItinerary(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    String trackingId = request.getParameter("trackingId");

    List<RouteCandidateDTO> routeCandidates = bookingServiceFacade.requestPossibleRoutesForCargo(trackingId);
    map.put("routeCandidates", routeCandidates);

    CargoRoutingDTO cargoDTO = bookingServiceFacade.loadCargoForRouting(trackingId);
    map.put("cargo", cargoDTO);

    return map;
  }

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

  public Map pickNewDestination(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();

    List<LocationDTO> locations = bookingServiceFacade.listShippingLocations();
    map.put("locations", locations);

    String trackingId = request.getParameter("trackingId");
    CargoRoutingDTO cargo = bookingServiceFacade.loadCargoForRouting(trackingId);
    map.put("cargo", cargo);

    return map;
  }

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
