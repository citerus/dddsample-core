package se.citerus.dddsample.application.web;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import se.citerus.dddsample.application.remoting.BookingServiceFacade;
import se.citerus.dddsample.application.remoting.dto.CargoRoutingDTO;
import se.citerus.dddsample.application.remoting.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.application.remoting.dto.LegDTO;
import se.citerus.dddsample.application.remoting.dto.LocationDTO;
import se.citerus.dddsample.application.web.command.RegistrationCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @see se.citerus.dddsample.application.web.CargoTrackingController
 */
public final class CargoAdminController extends MultiActionController {

  private BookingServiceFacade bookingServiceFacade;

  public Map registrationForm(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final Map<String, Object> map = new HashMap<String, Object>();
    final List<LocationDTO> dtoList = bookingServiceFacade.listShippingLocations();

    final List<String> unLocodeStrings = new ArrayList<String>();

    for (LocationDTO dto : dtoList) {
      unLocodeStrings.add(dto.getUnLocode());
    }

    map.put("unlocodes", unLocodeStrings);
    return map;
  }

  public void register(final HttpServletRequest request, final HttpServletResponse response,
                       final RegistrationCommand command) throws Exception {

    final String trackingId = bookingServiceFacade.registerNewCargo(
      command.getOriginUnlocode(), command.getDestinationUnlocode()
    );
    response.sendRedirect("show.html?trackingId=" + trackingId);
  }

  public Map list(HttpServletRequest request, HttpServletResponse response) throws Exception {
    final Map<String, Object> map = new HashMap<String, Object>();
    final List<CargoRoutingDTO> cargoList = bookingServiceFacade.listAllCargos();

    map.put("cargoList", cargoList);
    return map;
  }

  public Map show(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final Map<String, Object> map = new HashMap<String, Object>();
    final String trackingId = request.getParameter("trackingId");
    final CargoRoutingDTO dto = bookingServiceFacade.loadCargoForRouting(trackingId);
    map.put("cargo", dto);
    return map;
  }

  public Map selectItinerary(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final Map<String, Object> map = new HashMap<String, Object>();
    final String trackingId = request.getParameter("trackingId");
    final List<ItineraryCandidateDTO> itineraryCandidates = bookingServiceFacade.requestPossibleRoutesForCargo(trackingId);

    map.put("itineraryCandidates", itineraryCandidates);
    map.put("trackingId", trackingId);
    return map;
  }

  public void assignItinerary(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final String trackingId = request.getParameter("trackingId");

    // TODO:  gah, stuck on indexoutofbounds (legs[0].fromUnlocode etc) when trying to bind...
    // Revisit and fix this with a proper command object, this is just hideous
    final String[] cmIds = (String[]) request.getParameterMap().get("legs.carrierMovementId");
    final String[] fromUnlocodes = (String[]) request.getParameterMap().get("legs.fromUnlocode");
    final String[] toUnlocodes = (String[]) request.getParameterMap().get("legs.toUnlocode");

    final List<LegDTO> legDTOs = new ArrayList<LegDTO>(cmIds.length);
    for (int i = 0; i < cmIds.length; i++) {
      legDTOs.add(new LegDTO(cmIds[i], fromUnlocodes[i], toUnlocodes[i]));
    }

    final ItineraryCandidateDTO selectedItinerary = new ItineraryCandidateDTO(legDTOs);

    bookingServiceFacade.assignCargoToRoute(trackingId, selectedItinerary);

    response.sendRedirect("list.html");
  }

  public void setBookingServiceFacade(BookingServiceFacade bookingServiceFacade) {
    this.bookingServiceFacade = bookingServiceFacade;
  }
}
