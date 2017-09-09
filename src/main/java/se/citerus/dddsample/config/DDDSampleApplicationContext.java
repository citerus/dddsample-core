package se.citerus.dddsample.config;

import com.pathfinder.api.GraphTraversalService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.PlatformTransactionManager;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.BookingService;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.application.impl.BookingServiceImpl;
import se.citerus.dddsample.application.impl.CargoInspectionServiceImpl;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.dddsample.infrastructure.routing.ExternalRoutingService;

@Configuration
@ImportResource({
        "classpath:context-interfaces.xml",
        "classpath:context-infrastructure-messaging.xml",
        "classpath:context-infrastructure-persistence.xml"})
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

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Autowired
    SessionFactory sessionFactory;

    @Bean
    public BookingService bookingService() {
        return new BookingServiceImpl(cargoRepository, locationRepository, routingService);
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
        ExternalRoutingService routingService = new ExternalRoutingService();
        routingService.setGraphTraversalService(graphTraversalService);
        routingService.setLocationRepository(locationRepository);
        routingService.setVoyageRepository(voyageRepository);
        return routingService;
    }

    @Bean
    public SampleDataGenerator sampleDataGenerator() {
        return new SampleDataGenerator(platformTransactionManager, sessionFactory, cargoRepository, voyageRepository, locationRepository, handlingEventRepository);
    }
}
