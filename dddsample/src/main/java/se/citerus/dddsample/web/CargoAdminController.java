package se.citerus.dddsample.web;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.domain.UnLocode;
import se.citerus.dddsample.service.CargoService;
import se.citerus.dddsample.service.RoutingService;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.service.dto.LegDTO;
import se.citerus.dddsample.web.command.RegistrationCommand;
import se.citerus.dddsample.web.command.RoutingCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles cargo routing and administration.
 *
 */
public class CargoAdminController extends MultiActionController {

  private CargoService cargoService;
  private RoutingService routingService;

  public Map registrationForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map map = new HashMap();
    List<UnLocode> unLocodes = cargoService.shippingLocations();
    List<String> unLocodeStrings = new ArrayList<String>();
    for (UnLocode unLocode : unLocodes) {
      unLocodeStrings.add(unLocode.idString());
    }
    map.put("unlocodes", unLocodeStrings);
    return map;
  }

  public void register(HttpServletRequest request, HttpServletResponse response, RegistrationCommand command) throws Exception {
    TrackingId trackingId = cargoService.registerNew(
      new UnLocode(command.getOriginUnlocode()),
      new UnLocode(command.getDestinationUnlocode())
    );
    response.sendRedirect("show.html?trackingId=" + trackingId.idString());
  }

  public Map list(HttpServletRequest request, HttpServletResponse response) {
    Map map = new HashMap();
    map.put("cargoList", cargoService.loadAllForRouting());
    return map;
  }

  public Map show(HttpServletRequest request, HttpServletResponse response) {
    Map map = new HashMap();
    String param = request.getParameter("trackingId");
    TrackingId trackingId = new TrackingId(param);
    map.put("cargo", cargoService.loadForRouting(trackingId));
    return map;
  }

  public Map selectItinerary(HttpServletRequest request, HttpServletResponse response) {
    Map map = new HashMap();
    TrackingId trackingId = new TrackingId(request.getParameter("trackingId"));

    List<ItineraryCandidateDTO> itineraries = routingService.calculatePossibleRoutes(trackingId, null);

    List<RoutingCommand.ItineraryCandidateCommand> itineraryCandidates = new ArrayList<RoutingCommand.ItineraryCandidateCommand>();

    // TODO: eliminate the routing command altogether, use the DTOs in the view
    for (ItineraryCandidateDTO itinerary : itineraries) {
      RoutingCommand.ItineraryCandidateCommand itineraryCandidateCommand = new RoutingCommand.ItineraryCandidateCommand();
      itineraryCandidateCommand.setTrackingId(trackingId.idString());
      itineraryCandidates.add(itineraryCandidateCommand);
      for (LegDTO leg : itinerary.getLegs()) {
        RoutingCommand.LegCommand legCommand = new RoutingCommand.LegCommand();
        legCommand.setCarrierMovementId(leg.getCarrierMovementId());
        legCommand.setFromUnlocode(leg.getFrom());
        legCommand.setToUnlocode(leg.getTo());
        itineraryCandidateCommand.getLegs().add(legCommand);
      }
    }

    map.put("itineraryCandidates", itineraryCandidates);
    map.put("trackingId", trackingId.idString());
    return map;
  }

  public void assignItinerary(HttpServletRequest request, HttpServletResponse response) throws Exception {
    TrackingId trackingId = new TrackingId(request.getParameter("trackingId"));

    // TODO:  gah, stuck on indexoutofbounds (legs[0].fromUnlocode etc) when trying to bind...
    // Revisit and fix this with a proper command object, this is just hideous
    String[] cmIds = (String[]) request.getParameterMap().get("legs.carrierMovementId");
    String[] fromUnlocodes = (String[]) request.getParameterMap().get("legs.fromUnlocode");
    String[] toUnlocodes = (String[]) request.getParameterMap().get("legs.toUnlocode");

    List<LegDTO> legDTOs = new ArrayList<LegDTO>(cmIds.length);
    for (int i = 0; i < cmIds.length; i++) {
      legDTOs.add(new LegDTO(cmIds[i], fromUnlocodes[i], toUnlocodes[i]));
    }

    ItineraryCandidateDTO selectedItinerary = new ItineraryCandidateDTO(legDTOs);
    cargoService.assignItinerary(trackingId, selectedItinerary);

    response.sendRedirect("list.html");
  }

  public void setCargoService(CargoService cargoService) {
    this.cargoService = cargoService;
  }

  public void setRoutingService(RoutingService routingService) {
    this.routingService = routingService;
  }

}
