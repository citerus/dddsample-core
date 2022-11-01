package se.citerus.dddsample.interfaces.tracking.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.HandlingActivity;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Schedule;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CargoTrackingDTOConverterTest {
    private static final Location exampleLocation = new Location(new UnLocode("SESTO"), "Stockholm");
    private ResourceBundleMessageSource messageSource;

    @BeforeEach
    void setUp() throws IOException {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setCommonMessages(getLocalizationStrings());
    }

    @Test
    void shouldConvertValidCargoToDTO() {
        MessageSource mockMsgSrc = mock(MessageSource.class);
        when(mockMsgSrc.getMessage(anyString(), any(), eq("[Unknown status]"), eq(Locale.ENGLISH))).thenReturn("TEST-STATUS");
        Location origin = new Location(new UnLocode("SESTO"), "Stockholm");
        Location dest = new Location(new UnLocode("FIHEL"), "Helsinki");
        Date deadline = new Date();
        Cargo cargo = new Cargo(new TrackingId("TEST123"), new RouteSpecification(origin, dest, deadline));
        HandlingActivity handlingActivity = new HandlingActivity(HandlingEvent.Type.RECEIVE, origin);
        ReflectionTestUtils.setField(cargo.delivery(), "nextExpectedActivity", handlingActivity);
        ReflectionTestUtils.setField(cargo.delivery(), "eta", new Date(0));

        CargoTrackingDTO result = CargoTrackingDTOConverter.convert(cargo, emptyList(), mockMsgSrc, Locale.ENGLISH);

        assertThat(result).extracting("trackingId", "statusText", "destination", "nextExpectedActivity", "isMisdirected")
                .contains("TEST123", "TEST-STATUS", "Helsinki", "Next expected activity is to receive cargo in Stockholm", false);
        // assertThat(result.getEta()).isEqualTo("1970-01-01 00:00"); // TODO test this once we have added handling of timezones
    }

    @Test
    void shouldConvertValidCargoWithHandlingEventsToDTO() {
        MessageSource mockMsgSrc = mock(MessageSource.class);
        when(mockMsgSrc.getMessage(anyString(), any(), eq("[Unknown status]"), eq(Locale.ENGLISH))).thenReturn("TEST-STATUS");
        when(mockMsgSrc.getMessage(anyString(), any(), eq(Locale.ENGLISH))).thenReturn("TEST-DESCR");
        Location origin = new Location(new UnLocode("SESTO"), "Stockholm");
        Location dest = new Location(new UnLocode("FIHEL"), "Helsinki");
        Date deadline = new Date();
        Cargo cargo = new Cargo(new TrackingId("TEST123"), new RouteSpecification(origin, dest, deadline));
        HandlingActivity handlingActivity = new HandlingActivity(HandlingEvent.Type.RECEIVE, origin);
        ReflectionTestUtils.setField(cargo.delivery(), "nextExpectedActivity", handlingActivity);
        ReflectionTestUtils.setField(cargo.delivery(), "eta", new Date(0));

        Voyage voyage = new Voyage(new VoyageNumber("0101"), new Schedule(Collections.singletonList(
                new CarrierMovement(origin, dest, new Date(), new Date()))));
        List<HandlingEvent> events = Arrays.asList(
                new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, origin),
                new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, origin, voyage));
        CargoTrackingDTO result = CargoTrackingDTOConverter.convert(cargo, events, mockMsgSrc, Locale.ENGLISH);

        assertThat(result).extracting("trackingId", "statusText", "destination", "nextExpectedActivity", "isMisdirected")
                .contains("TEST123", "TEST-STATUS", "Helsinki", "Next expected activity is to receive cargo in Stockholm", false);
        // assertThat(result.getEta()).isEqualTo("1970-01-01 00:00"); // TODO test this once timezone handling has been added
        assertThat(result.handlingEvents)
                .hasSize(2)
                .element(0)
                .extracting("location", "type", "voyageNumber", "isExpected", "description")
                .containsExactly("Stockholm", "RECEIVE", "", true, "TEST-DESCR"); // TODO test time field once timezone handling has been added
    }

    @Disabled("disabled due to missing timezone handling") // TODO enable once timezone handling has been added
    @CsvSource(value = {"LOAD;Loaded onto voyage 0101 in Stockholm, at 11/1/22 9:37 PM.", "UNLOAD;Unloaded off voyage 0101 in Stockholm, at 11/1/22 9:37 PM."}, delimiter = ';')
    @ParameterizedTest
    void shouldConvertDescriptionCorrectlyForGivenParamsWithVoyage(String eventType, String expectedOutput) throws IOException {
        Voyage voyage = exampleVoyage();
        Date date = new Date(LocalDateTime.of(2022, 11, 1, 13, 37).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
        HandlingEvent event = new HandlingEvent(exampleCargo(), date, date,
                HandlingEvent.Type.valueOf(eventType), exampleLocation, voyage);

        String description = CargoTrackingDTOConverter.convertDescription(event, messageSource, Locale.ENGLISH);

        assertThat(description).isNotNull().isEqualTo(expectedOutput);
    }

    @Disabled("disabled due to missing timezone handling") // TODO enable once timezone handling has been added
    @CsvSource(value = {"RECEIVE;Received in Stockholm, at 11/1/22 9:37 PM.", "CLAIM;Claimed in Stockholm, at 11/1/22 9:37 PM.", "CUSTOMS;Cleared customs in Stockholm, at 11/1/22 9:37 PM."}, delimiter = ';')
    @ParameterizedTest
    void shouldConvertDescriptionCorrectlyForGivenParamsWithoutVoyage(String eventType, String expectedOutput) throws IOException {
        Date date = new Date(LocalDateTime.of(2022, 11, 1, 13, 37).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
        HandlingEvent event = new HandlingEvent(exampleCargo(), date, date,
                HandlingEvent.Type.valueOf(eventType), exampleLocation);

        String description = CargoTrackingDTOConverter.convertDescription(event, messageSource, Locale.ENGLISH);

        assertThat(description).isNotNull().isEqualTo(expectedOutput);
    }

    @ArgumentsSource(StatusTextArgsProvider.class)
    @ParameterizedTest
    void shouldConvertStatusTextCorrectlyForGivenParams(Cargo cargo, String expectedOutput) throws IOException {
        String description = CargoTrackingDTOConverter.convertStatusText(cargo, messageSource, Locale.ENGLISH);

        assertThat(description).isNotNull().isEqualTo(expectedOutput);
    }

    @ArgumentsSource(NextExpectedActivityArgsProvider.class)
    @ParameterizedTest
    void shouldConvertNextExpectedActivityForGivenParams(HandlingActivity handlingActivity, String expectedOutput) {
        Cargo cargo = exampleCargo();
        ReflectionTestUtils.setField(cargo.delivery(), "nextExpectedActivity", handlingActivity);

        String result = CargoTrackingDTOConverter.convertNextExpectedActivity(cargo);

        assertThat(result).isEqualTo(expectedOutput);
    }

    private static class StatusTextArgsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(createHandlingEvent(HandlingEvent.Type.LOAD, exampleVoyage()), "Onboard voyage 0101"),
                Arguments.of(createHandlingEvent(HandlingEvent.Type.UNLOAD, exampleVoyage()), "In port Stockholm"),
                Arguments.of(createHandlingEvent(HandlingEvent.Type.RECEIVE), "In port Stockholm"),
                Arguments.of(createHandlingEvent(HandlingEvent.Type.CUSTOMS), "In port Stockholm"),
                Arguments.of(createHandlingEvent(HandlingEvent.Type.CLAIM), "Claimed")
            );
        }

        private Cargo createHandlingEvent(HandlingEvent.Type eventType) {
            Cargo cargo = exampleCargo();
            Location origin = new Location(new UnLocode("SESTO"), "Stockholm");
            HandlingEvent handlingEvent = new HandlingEvent(cargo, new Date(), new Date(), eventType, origin);
            cargo.deriveDeliveryProgress(new HandlingHistory(singletonList(handlingEvent)));
            return cargo;
        }

        private Cargo createHandlingEvent(HandlingEvent.Type eventType, Voyage voyage) {
            Cargo cargo = exampleCargo();
            Location origin = new Location(new UnLocode("SESTO"), "Stockholm");
            HandlingEvent handlingEvent = new HandlingEvent(cargo, new Date(), new Date(), eventType, origin, voyage);
            cargo.deriveDeliveryProgress(new HandlingHistory(singletonList(handlingEvent)));
            return cargo;
        }
    }

    private static class NextExpectedActivityArgsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            Location origin = new Location(new UnLocode("SESTO"), "Stockholm");
            return Stream.of(
                    Arguments.of(new HandlingActivity(HandlingEvent.Type.LOAD, origin, exampleVoyage()), "Next expected activity is to load cargo onto voyage 0101 in Stockholm"),
                    Arguments.of(new HandlingActivity(HandlingEvent.Type.UNLOAD, origin, exampleVoyage()), "Next expected activity is to unload cargo off of 0101 in Stockholm"),
                    Arguments.of(new HandlingActivity(HandlingEvent.Type.RECEIVE, origin), "Next expected activity is to receive cargo in Stockholm"),
                    Arguments.of(new HandlingActivity(HandlingEvent.Type.CLAIM, origin), "Next expected activity is to claim cargo in Stockholm"),
                    Arguments.of(new HandlingActivity(HandlingEvent.Type.CUSTOMS, origin), "Next expected activity is to customs cargo in Stockholm")
            );
        }
    }

    private static Voyage exampleVoyage() {
        Location origin = new Location(new UnLocode("SESTO"), "Stockholm");
        Location dest = new Location(new UnLocode("FIHEL"), "Helsinki");
        return new Voyage(new VoyageNumber("0101"), new Schedule(Collections.singletonList(
                new CarrierMovement(origin, dest, new Date(), new Date()))));
    }

    private Properties getLocalizationStrings() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages_en.properties"));
        return properties;
    }

    private static Cargo exampleCargo() {
        Location origin = new Location(new UnLocode("SESTO"), "Stockholm");
        Location dest = new Location(new UnLocode("FIHEL"), "Helsinki");
        Date deadline = new Date();
        return new Cargo(new TrackingId("TEST123"), new RouteSpecification(origin, dest, deadline));
    }
}