package se.citerus.dddsample.tracking.bookingui.web;

import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import se.citerus.dddsample.tracking.booking.api.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.StringWriter;
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
 */
public final class CargoAdminController extends MultiActionController {

  private BookingServiceFacade bookingServiceFacade;
  private static final CustomDateEditor DATE_EDITOR = new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), false);
  private static final UnLocodeComparator UN_LOCODE_COMPARATOR = new UnLocodeComparator();

  public CargoAdminController(BookingServiceFacade bookingServiceFacade) {
    this.bookingServiceFacade = bookingServiceFacade;
  }

  @Override
  protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    super.initBinder(request, binder);
    binder.registerCustomEditor(Date.class, DATE_EDITOR);
  }

  public Map cargoBookingForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();

    List<LocationDTO> dtoList = bookingServiceFacade.listShippingLocations();
    Collections.sort(dtoList, UN_LOCODE_COMPARATOR);
    
    map.put("locations", dtoList);
    return map;
  }

  public void bookCargo(HttpServletRequest request, HttpServletResponse response,
                        CargoBookingCommand command) throws Exception {
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

  public Map voyageDelayedForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, List<String>> departures = new HashMap<String, List<String>>();
    Map<String, List<String>> arrivals = new HashMap<String, List<String>>();

    List<VoyageDTO> voyages = bookingServiceFacade.listAllVoyages();

    for (VoyageDTO voyage : voyages) {
      List<String> departureLocations = getLocationsList(departures, voyage);
      List<String> arrivalLocations = getLocationsList(arrivals, voyage);
      for (CarrierMovementDTO dto : voyage.getMovements()) {
        departureLocations.add(dto.getDepartureLocation().getUnLocode());
        arrivalLocations.add(dto.getArrivalLocation().getUnLocode());
      }
    }

    Map<String, Object> model = new HashMap<String, Object>();

    model.put("departures", toJSON(departures));
    model.put("arrivals", toJSON(arrivals));
    model.put("voyages", voyages);

    return model;
  }

  public void voyageDelayed(HttpServletRequest request, HttpServletResponse response, VoyageDelayCommand command) throws Exception {
    if (command.getType() == VoyageDelayCommand.DelayType.DEPT) {
      bookingServiceFacade.departureDelayed(new VoyageDelayDTO(command.getVoyageNumber(), command.getHours() * 60));
    } else if (command.getType() == VoyageDelayCommand.DelayType.ARR) {
      bookingServiceFacade.arrivalDelayed(new VoyageDelayDTO(command.getVoyageNumber(), command.getHours() * 60));
    }

    response.sendRedirect("list.html");
  }

  private String toJSON(Map<String, List<String>> locationMap) throws XMLStreamException {
    StringWriter stringWriter = new StringWriter();
    MappedNamespaceConvention con = new MappedNamespaceConvention();
    AbstractXMLStreamWriter w = new MappedXMLStreamWriter(con, stringWriter);

    w.writeStartDocument();
    for (Map.Entry<String, List<String>> e : locationMap.entrySet()) {
      for (String location : e.getValue()) {
        w.writeStartElement(e.getKey());
        w.writeCharacters(location);
        w.writeEndElement();
      }
    }
    w.writeEndDocument();

    return stringWriter.toString();
  }

  private List<String> getLocationsList(Map<String, List<String>> map, VoyageDTO voyage) {
    List<String> locations = map.get(voyage.getVoyageNumber());
    if (locations == null) {
      locations = new ArrayList<String>();
      map.put(voyage.getVoyageNumber(), locations);
    }
    return locations;
  }

  private static class UnLocodeComparator implements Comparator<LocationDTO> {
    @Override
    public int compare(LocationDTO o1, LocationDTO o2) {
      return o1.getUnLocode().compareTo(o2.getUnLocode());
    }
  }
}
