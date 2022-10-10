package se.citerus.dddsample.interfaces.booking.web;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import se.citerus.dddsample.application.BookingService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO;
import se.citerus.dddsample.interfaces.booking.facade.internal.assembler.CargoRoutingDTOAssembler;
import se.citerus.dddsample.interfaces.booking.facade.internal.assembler.ItineraryCandidateDTOAssembler;
import se.citerus.dddsample.interfaces.booking.facade.internal.assembler.LocationDTOAssembler;

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

    private final LocationRepository locationRepository;
    private final BookingService bookingService;
    private final CargoRepository cargoRepository;
    private final VoyageRepository voyageRepository;

    public CargoAdminController(LocationRepository locationRepository, BookingService bookingService, CargoRepository cargoRepository, VoyageRepository voyageRepository) {
        this.locationRepository = locationRepository;
        this.bookingService = bookingService;
        this.cargoRepository = cargoRepository;
        this.voyageRepository = voyageRepository;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), false));
    }

    @RequestMapping("/registration")
    public String registration(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        List<LocationDTO> dtoList = listAllCargo();

        List<String> unLocodeStrings = new ArrayList<>();

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
        TrackingId trackingId = bookingService.bookNewCargo(
                new UnLocode(command.getOriginUnlocode()),
                new UnLocode(command.getDestinationUnlocode()),
                arrivalDeadline
        );
        response.sendRedirect("show?trackingId=" + trackingId.idString());
    }

    @RequestMapping("/list")
    public String list(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        final List<Cargo> cargoList = cargoRepository.getAll();
        final List<CargoRoutingDTO> dtoList = new ArrayList<>(cargoList.size());
        for (Cargo cargo : cargoList) {
            dtoList.add(CargoRoutingDTOAssembler.toDTO(cargo));
        }

        model.put("cargoList", dtoList);
        return "admin/list";
    }

    @RequestMapping("/show")
    public String show(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        String trackingId = request.getParameter("trackingId");
        CargoRoutingDTO dto = loadCargoForRouting(trackingId);
        model.put("cargo", dto);
        return "admin/show";
    }

    @RequestMapping("/selectItinerary")
    public String selectItinerary(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        String trackingId = request.getParameter("trackingId");

        final List<Itinerary> itineraries = bookingService.requestPossibleRoutesForCargo(new TrackingId(trackingId));

        final List<RouteCandidateDTO> routeCandidates = new ArrayList<>(itineraries.size());
        for (Itinerary itinerary : itineraries) {
            routeCandidates.add(ItineraryCandidateDTOAssembler.toDTO(itinerary));
        }
        model.put("routeCandidates", routeCandidates);

        CargoRoutingDTO cargoDTO = loadCargoForRouting(trackingId);
        model.put("cargo", cargoDTO);

        return "admin/selectItinerary";
    }

    @RequestMapping(value = "/assignItinerary", method = RequestMethod.POST)
    public void assignItinerary(HttpServletRequest request, HttpServletResponse response, RouteAssignmentCommand command) throws Exception {
        List<LegDTO> legDTOs = new ArrayList<>(command.getLegs().size());
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

        final Itinerary itinerary = ItineraryCandidateDTOAssembler.fromDTO(selectedRoute, voyageRepository, locationRepository);
        final TrackingId trackingId = new TrackingId(command.getTrackingId());

        bookingService.assignCargoToRoute(itinerary, trackingId);

        response.sendRedirect("show?trackingId=" + command.getTrackingId());
    }

    @RequestMapping(value = "/pickNewDestination")
    public String pickNewDestination(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        List<LocationDTO> locations = listAllCargo();
        model.put("locations", locations);

        String trackingId = request.getParameter("trackingId");
        CargoRoutingDTO cargo = loadCargoForRouting(trackingId);
        model.put("cargo", cargo);

        return "admin/pickNewDestination";
    }

    @RequestMapping(value = "/changeDestination", method = RequestMethod.POST)
    public void changeDestination(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String trackingId = request.getParameter("trackingId");
        String unLocode = request.getParameter("unlocode");
        bookingService.changeDestination(new TrackingId(trackingId), new UnLocode(unLocode));
        response.sendRedirect("show?trackingId=" + trackingId);
    }

    private CargoRoutingDTO loadCargoForRouting(String trackingId) {
        final Cargo cargo = cargoRepository.find(new TrackingId(trackingId));
        return CargoRoutingDTOAssembler.toDTO(cargo);
    }

    private List<LocationDTO> listAllCargo() {
        final List<Location> allLocations = locationRepository.getAll();
        return LocationDTOAssembler.toDTOList(allLocations);
    }
}
