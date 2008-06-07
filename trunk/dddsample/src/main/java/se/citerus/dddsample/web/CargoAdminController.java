package se.citerus.dddsample.web;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.domain.UnLocode;
import se.citerus.dddsample.service.CargoService;
import se.citerus.dddsample.service.RoutingService;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.service.dto.LegDTO;
import se.citerus.dddsample.web.command.RegistrationCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles cargo routing and administration.
 */
public final class CargoAdminController extends MultiActionController {

  private CargoService cargoService;
  private RoutingService routingService;

  public Map registrationForm(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final Map<String, Object> map = new HashMap<String, Object>();
    final List<UnLocode> unLocodes = cargoService.shippingLocations();
    final List<String> unLocodeStrings = new ArrayList<String>();

    for (UnLocode unLocode : unLocodes) {
      unLocodeStrings.add(unLocode.idString());
    }

    map.put("unlocodes", unLocodeStrings);
    return map;
  }

  public void register(final HttpServletRequest request, final HttpServletResponse response,
                       final RegistrationCommand command) throws Exception {

    final TrackingId trackingId = cargoService.registerNew(
      new UnLocode(command.getOriginUnlocode()),
      new UnLocode(command.getDestinationUnlocode())
    );
    response.sendRedirect("show.html?trackingId=" + trackingId.idString());
  }

  public Map list(HttpServletRequest request, HttpServletResponse response) {
    final Map<String, Object> map = new HashMap<String, Object>();
    map.put("cargoList", cargoService.loadAllForRouting());
    return map;
  }

  public Map show(final HttpServletRequest request, final HttpServletResponse response) {
    final Map<String, Object> map = new HashMap<String, Object>();
    final TrackingId trackingId = new TrackingId(request.getParameter("trackingId"));
    map.put("cargo", cargoService.loadForRouting(trackingId));
    return map;
  }

  public Map selectItinerary(final HttpServletRequest request, final HttpServletResponse response) {
    final Map<String, Object> map = new HashMap<String, Object>();
    final TrackingId trackingId = new TrackingId(request.getParameter("trackingId"));
    final List<ItineraryCandidateDTO> itineraries = routingService.calculatePossibleRoutes(trackingId, null);

    map.put("itineraryCandidates", itineraries);
    map.put("trackingId", trackingId.idString());
    return map;
  }

  public void assignItinerary(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final TrackingId trackingId = new TrackingId(request.getParameter("trackingId"));

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
    cargoService.assignItinerary(trackingId, selectedItinerary);

    response.sendRedirect("list.html");
  }

  public void setCargoService(final CargoService cargoService) {
    this.cargoService = cargoService;
  }

  public void setRoutingService(final RoutingService routingService) {
    this.routingService = routingService;
  }

}
