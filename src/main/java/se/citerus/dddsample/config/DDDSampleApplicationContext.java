package se.citerus.dddsample.config;

import com.pathfinder.api.GraphTraversalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.BookingService;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.application.impl.BookingServiceImpl;
import se.citerus.dddsample.application.impl.CargoInspectionServiceImpl;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.domain.model.cargo.CargoFactory;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.dddsample.infrastructure.messaging.jms.InfrastructureMessagingJmsConfig;
import se.citerus.dddsample.infrastructure.routing.ExternalRoutingService;
import se.citerus.dddsample.infrastructure.sampledata.SampleDataGenerator;
import se.citerus.dddsample.interfaces.InterfacesApplicationContext;

import javax.persistence.EntityManager;

@Configuration
@Import({InterfacesApplicationContext.class, InfrastructureMessagingJmsConfig.class})
public class DDDSampleApplicationContext {

    @Autowired
    CargoRepository cargoRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    VoyageRepository voyageRepository;

    @Autowired
    GraphTraversalService graphTraversalService;

    @Autowired
    RoutingService routingService;

    @Autowired
    HandlingEventFactory handlingEventFactory;

    @Autowired
    HandlingEventRepository handlingEventRepository;

    @Autowired
    ApplicationEvents applicationEvents;

    @Bean
    public CargoFactory cargoFactory() {
        return new CargoFactory(locationRepository, cargoRepository);
    }

    @Bean
    public BookingService bookingService(CargoFactory cargoFactory) {
        return new BookingServiceImpl(cargoRepository, locationRepository, routingService, cargoFactory);
    }

    @Bean
    public CargoInspectionService cargoInspectionService() {
        return new CargoInspectionServiceImpl(applicationEvents, cargoRepository, handlingEventRepository);
    }

    @Bean
    public HandlingEventService handlingEventService() {
        return new HandlingEventServiceImpl(handlingEventRepository, applicationEvents, handlingEventFactory);
    }

    @Bean
    public HandlingEventFactory handlingEventFactory() {
        return new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    }

    @Bean
    public RoutingService routingService() {
        return new ExternalRoutingService(graphTraversalService, locationRepository, voyageRepository);
    }

    @Bean
    public SampleDataGenerator sampleDataGenerator(CargoRepository cargoRepository, VoyageRepository voyageRepository,
                                                   LocationRepository locationRepository, HandlingEventRepository handlingEventRepository,
                                                   PlatformTransactionManager platformTransactionManager, EntityManager entityManager) {
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(cargoRepository, voyageRepository, locationRepository, handlingEventRepository, platformTransactionManager);
        try {
            sampleDataGenerator.generate(); // TODO investigate if this can be called with initMethod in the annotation
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return sampleDataGenerator;
    }
}
