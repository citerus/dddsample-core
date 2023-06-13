package se.citerus.dddsample.interfaces.tracking.ws;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriTemplate;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.rmi.RemoteException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class CargoTrackingRestService {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CargoRepository cargoRepository;
    private final BookingServiceFacade bookingServiceFacade;
    private final HandlingEventRepository handlingEventRepository;
    private final MessageSource messageSource;

    public CargoTrackingRestService(CargoRepository cargoRepository, BookingServiceFacade bookingServiceFacade, HandlingEventRepository handlingEventRepository, MessageSource messageSource) {
        this.cargoRepository = cargoRepository;
        this.bookingServiceFacade = bookingServiceFacade;
        this.handlingEventRepository = handlingEventRepository;
        this.messageSource = messageSource;
    }

    @GetMapping(value = "/api/cargo/{trackingId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CargoTrackingDTO> trackCargoById(final HttpServletRequest request,
                                                       @PathVariable("trackingId") String trackingId) {
        try {
            Locale locale = RequestContextUtils.getLocale(request);
            TrackingId trkId = new TrackingId(trackingId);
            Cargo cargo = cargoRepository.find(trkId);
            System.out.println("voyage " + cargo.itinerary().legs().get(0).voyage().toString());
            if (cargo == null) {
                throw new NotFoundException("No cargo found for trackingId");
            }
            final List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trkId)
                    .distinctEventsByCompletionTime();
            return ResponseEntity.ok(CargoTrackingDTOConverter.convert(cargo, handlingEvents, messageSource, locale));
        } catch (NotFoundException e) {
            URI uri = new UriTemplate(request.getContextPath() + "/api/track/{trackingId}").expand(trackingId);
            return ResponseEntity.notFound().location(uri).build();
        } catch (Exception e) {
            log.error("Unexpected error in trackCargo endpoint", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping(value = "/api/cargo/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CargoTrackingDTO>> trackCargo(final HttpServletRequest request) {
        try {
            Locale locale = RequestContextUtils.getLocale(request);
            List<Cargo> cargo = cargoRepository.getAll();
            if (cargo == null) {
                throw new NotFoundException("No cargo found for trackingId");
            }
            List<CargoTrackingDTO> cargoList = cargo.stream().map(c -> {
                List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(new TrackingId(c.trackingId))
                        .distinctEventsByCompletionTime();
                return CargoTrackingDTOConverter.convert(c, handlingEvents, messageSource, locale);
            }).collect(Collectors.toList());

            return ResponseEntity.ok(cargoList);
        } catch (Exception e) {
            log.error("Unexpected error in trackCargo endpoint", e);
            return ResponseEntity.status(500).build();
        }
    }


    @PostMapping(value = "/api/cargo/", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<RegistrationResponseDTO> registerCargo(HttpServletRequest request, HttpServletResponse response,
                                                            @RequestBody CargoRegistrationDTO cargoRegistrationDTO) throws Exception {
        try {
            Instant arrivalDeadline = Instant.parse(cargoRegistrationDTO.arrivalDeadline);
            String trackingId = bookingServiceFacade.bookNewCargo(cargoRegistrationDTO.origin, cargoRegistrationDTO.destination, arrivalDeadline);
            return ResponseEntity.ok(new RegistrationResponseDTO(trackingId));
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/api/unlocodes", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LocationDTO>> getUnlocodes(final HttpServletRequest request) throws Exception {
        List<LocationDTO> dtoList = bookingServiceFacade.listShippingLocations();
        return ResponseEntity.ok(dtoList);
    }
}
