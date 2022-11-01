package se.citerus.dddsample.interfaces.tracking.ws;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriTemplate;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class CargoTrackingRestService {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CargoRepository cargoRepository;
    private final HandlingEventRepository handlingEventRepository;
    private final MessageSource messageSource;

    public CargoTrackingRestService(CargoRepository cargoRepository, HandlingEventRepository handlingEventRepository, MessageSource messageSource) {
        this.cargoRepository = cargoRepository;
        this.handlingEventRepository = handlingEventRepository;
        this.messageSource = messageSource;
    }

    @GetMapping(value = "/api/track/{trackingId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CargoTrackingDTO> trackCargo(final HttpServletRequest request,
                                                       @PathVariable("trackingId") String trackingId) {
        try {
            Locale locale = RequestContextUtils.getLocale(request);
            TrackingId trkId = new TrackingId(trackingId);
            Cargo cargo = cargoRepository.find(trkId);
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
}
