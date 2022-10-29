package se.citerus.dddsample.interfaces;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.BookingService;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade;
import se.citerus.dddsample.interfaces.booking.facade.internal.BookingServiceFacadeImpl;
import se.citerus.dddsample.interfaces.booking.web.CargoAdminController;
import se.citerus.dddsample.interfaces.handling.file.UploadDirectoryScanner;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReportService;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReportServiceImpl;
import se.citerus.dddsample.interfaces.tracking.CargoTrackingController;
import se.citerus.dddsample.interfaces.tracking.TrackCommandValidator;

import java.io.File;
import java.util.Locale;

@Configuration
public class InterfacesApplicationContext implements WebMvcConfigurer {

    @Value("${uploadDirectory}")
    public String uploadDirectory;

    @Value("${parseFailureDirectory}")
    public String parseFailureDirectory;

    @Autowired
    public SessionFactory sessionFactory;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Bean
    public FixedLocaleResolver localeResolver() {
        FixedLocaleResolver fixedLocaleResolver = new FixedLocaleResolver();
        fixedLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return fixedLocaleResolver;
    }

    @Bean
    public CargoTrackingController cargoTrackingController(MessageSource messageSource, CargoRepository cargoRepository, HandlingEventRepository handlingEventRepository) {
        return new CargoTrackingController(cargoRepository, handlingEventRepository, messageSource);
    }

    @Bean
    public HandlingReportService handlingReportService(ApplicationEvents applicationEvents) {
        return new HandlingReportServiceImpl(applicationEvents);
    }

    @Bean
    public TrackCommandValidator trackCommandValidator() {
        return new TrackCommandValidator();
    }

    @Bean
    public CargoAdminController cargoAdminController(BookingServiceFacade bookingServiceFacade) {
        return new CargoAdminController(bookingServiceFacade);
    }

    @Bean
    public BookingServiceFacade bookingServiceFacade(BookingService bookingService, LocationRepository locationRepository, CargoRepository cargoRepository, VoyageRepository voyageRepository) {
        return new BookingServiceFacadeImpl(bookingService, locationRepository, cargoRepository, voyageRepository);
    }

    @Bean
    public UploadDirectoryScanner uploadDirectoryScanner(ApplicationEvents applicationEvents) {
        File uploadDirectoryFile = new File(uploadDirectory);
        File parseFailureDirectoryFile = new File(parseFailureDirectory);
        return new UploadDirectoryScanner(uploadDirectoryFile, parseFailureDirectoryFile, applicationEvents);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        OpenSessionInViewInterceptor openSessionInViewInterceptor = new OpenSessionInViewInterceptor();
        openSessionInViewInterceptor.setSessionFactory(sessionFactory);
        registry.addWebRequestInterceptor(openSessionInViewInterceptor);
    }

    @Bean
    public ThreadPoolTaskScheduler myScheduler(UploadDirectoryScanner scanner){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.scheduleAtFixedRate(scanner, 5000);
        return threadPoolTaskScheduler;
    }
}
