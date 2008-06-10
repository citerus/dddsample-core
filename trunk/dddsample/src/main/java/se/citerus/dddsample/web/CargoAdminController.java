package se.citerus.dddsample.web;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.LocationRepository;
import se.citerus.dddsample.service.CargoService;
import se.citerus.dddsample.service.RoutingService;
import se.citerus.dddsample.service.dto.CargoRoutingDTO;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.service.dto.LegDTO;
import se.citerus.dddsample.service.dto.assembler.CargoRoutingDTOAssembler;
import se.citerus.dddsample.service.dto.assembler.ItineraryCandidateDTOAssembler;
import se.citerus.dddsample.web.command.RegistrationCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Handles cargo routing and administration.
 *
 */
public final class CargoAdminController extends MultiActionController {

  private CargoService cargoService;
  private RoutingService routingService;
  private LocationRepository locationRepository;
  private CarrierMovementRepository carrierMovementRepository;

  // DTO conversion is pushed out to above the service layer for the time being,
  // pending a dedicated DTO remoting layer  

  public Map registrationForm(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final Map<String, Object> map = new HashMap<String, Object>();
    final List<UnLocode> unLocodes = cargoService.listShippingLocations();
    final List<String> unLocodeStrings = new ArrayList<String>();

    for (UnLocode unLocode : unLocodes) {
      unLocodeStrings.add(unLocode.idString());
    }

    map.put("unlocodes", unLocodeStrings);
    return map;
  }

  public void register(final HttpServletRequest request, final HttpServletResponse response,
                       final RegistrationCommand command) throws Exception {

    final TrackingId trackingId = cargoService.registerNewCargo(
      new UnLocode(command.getOriginUnlocode()),
      new UnLocode(command.getDestinationUnlocode())
    );
    response.sendRedirect("show.html?trackingId=" + trackingId.idString());
  }

  public Map list(HttpServletRequest request, HttpServletResponse response) {
    final Map<String, Object> map = new HashMap<String, Object>();
    final List<Cargo> allCargos = cargoService.listAllCargos();

    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();
    final List<CargoRoutingDTO> dtoList = new ArrayList<CargoRoutingDTO>(allCargos.size());

    for (Cargo cargo : allCargos) {
      dtoList.add(assembler.toDTO(cargo));
    }

    map.put("cargoList", dtoList);
    return map;
  }

  public Map show(final HttpServletRequest request, final HttpServletResponse response) {
    final Map<String, Object> map = new HashMap<String, Object>();
    final TrackingId trackingId = new TrackingId(request.getParameter("trackingId"));
    final Cargo cargo = cargoService.loadCargoForRouting(trackingId);
    final CargoRoutingDTO dto = new CargoRoutingDTOAssembler().toDTO(cargo);
    map.put("cargo", dto);
    return map;
  }

  public Map selectItinerary(final HttpServletRequest request, final HttpServletResponse response) {
    final Map<String, Object> map = new HashMap<String, Object>();
    final TrackingId trackingId = new TrackingId(request.getParameter("trackingId"));

    final Cargo cargo = cargoService.loadCargoForRouting(trackingId);
    final RouteSpecification routeSpecification = RouteSpecification.forCargo(cargo, new Date());
    final List<Itinerary> itineraries = routingService.requestPossibleRoutes(routeSpecification);

    final List<ItineraryCandidateDTO> itineraryCandidates = new ArrayList<ItineraryCandidateDTO>(itineraries.size());
    final ItineraryCandidateDTOAssembler dtoAssembler = new ItineraryCandidateDTOAssembler();
    for (Itinerary itinerary : itineraries) {
      itineraryCandidates.add(dtoAssembler.toDTO(itinerary));
    }

    map.put("itineraryCandidates", itineraryCandidates);
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
    final Itinerary itinerary = new ItineraryCandidateDTOAssembler().fromDTO(selectedItinerary, carrierMovementRepository, locationRepository);

    cargoService.assignCargoToRoute(trackingId, itinerary);

    response.sendRedirect("list.html");
  }

  public void setCargoService(final CargoService cargoService) {
    this.cargoService = cargoService;
  }

  public void setRoutingService(final RoutingService routingService) {
    this.routingService = routingService;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }
}
