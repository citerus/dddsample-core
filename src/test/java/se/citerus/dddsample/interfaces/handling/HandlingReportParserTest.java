package se.citerus.dddsample.interfaces.handling;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HandlingReportParserTest {

    @ParameterizedTest
    @NullSource
    public void shouldThrowErrorOnParsingNullUnloCode(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseUnLocode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse UNLO code: null");
    }

    @ParameterizedTest
    @EmptySource
    public void shouldThrowErrorOnParsingEmptyUnloCode(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseUnLocode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse UNLO code: ");
    }

    @ParameterizedTest
    @ValueSource(strings = {"XXX"})
    public void shouldThrowErrorOnParsingInvalidUnloCode(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseUnLocode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse UNLO code: XXX");
    }

    @ParameterizedTest
    @ValueSource(strings = {"SESTO"})
    public void shouldReturnUnloCodeOnParsingValidUnloCode(String input) {
        UnLocode result = HandlingReportParser.parseUnLocode(input);
        assertThat(result).isNotNull().extracting("unlocode").contains(input);
    }

    @ParameterizedTest
    @NullSource
    public void shouldThrowErrorOnParsingNullTrackingId(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseTrackingId(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse trackingId: null");
    }

    @ParameterizedTest
    @EmptySource
    public void shouldThrowErrorOnParsingEmptyTrackingId(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseTrackingId(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse trackingId: ");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC123"})
    public void shouldReturnTrackingIdOnParsingValidTrackingId(String input) {
        TrackingId result = HandlingReportParser.parseTrackingId(input);
        assertThat(result).isNotNull().extracting("id").contains(input);
    }

    @ParameterizedTest
    @NullSource
    public void shouldReturnNullOnParsingNullVoyageNumber(String input) {
        VoyageNumber voyageNumber = HandlingReportParser.parseVoyageNumber(input);
        assertThat(voyageNumber).isNull();
    }

    @ParameterizedTest
    @EmptySource
    public void shouldReturnNullOnParsingEmptyVoyageNumber(String input) {
        VoyageNumber voyageNumber = HandlingReportParser.parseVoyageNumber(input);
        assertThat(voyageNumber).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0101"})
    public void shouldReturnVoyageNumberOnParsingValidVoyageNumber(String input) {
        VoyageNumber result = HandlingReportParser.parseVoyageNumber(input);
        assertThat(result).isNotNull().extracting("number").contains(input);
    }

    @ParameterizedTest
    @NullSource
    public void shouldThrowErrorOnParsingNullHandlingEventType(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseEventType(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name is null");
    }

    @ParameterizedTest
    @EmptySource
    public void shouldThrowErrorOnParsingEmptyHandlingEventType(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseEventType(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(" is not a valid handling event type. Valid types are: [LOAD, UNLOAD, RECEIVE, CLAIM, CUSTOMS]");
    }

    @ParameterizedTest
    @ValueSource(strings = {"XXX"})
    public void shouldThrowErrorOnParsingInvalidHandlingEventType(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseEventType(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("XXX is not a valid handling event type. Valid types are: [LOAD, UNLOAD, RECEIVE, CLAIM, CUSTOMS]");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "LOAD",
            "UNLOAD",
            "RECEIVE",
            "CLAIM",
            "CUSTOMS"
    })
    public void shouldReturnHandlingEventTypeOnParsingValidHandlingEventType(String input) {
        HandlingEvent.Type result = HandlingReportParser.parseEventType(input);
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(input);
    }

    @ParameterizedTest
    @NullSource
    public void shouldThrowErrorOnParsingNullDate(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseDate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date format: null, must be on ISO 8601 format: yyyy-MM-dd HH:mm");
    }

    @ParameterizedTest
    @EmptySource
    public void shouldThrowErrorOnParsingEmptyDate(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseDate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date format: , must be on ISO 8601 format: yyyy-MM-dd HH:mm");
    }

    @ParameterizedTest
    @ValueSource(strings = {"XXX"})
    public void shouldThrowErrorOnParsingInvalidDate(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseDate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date format: XXX, must be on ISO 8601 format: yyyy-MM-dd HH:mm");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2022-10-29 13:37"
    })
    public void shouldReturnDateOnParsingValidDate(String input) {
        Date result = HandlingReportParser.parseDate(input);
        assertThat(result).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    public void shouldThrowErrorOnParsingNullCompletionTime(LocalDateTime input) {
        assertThatThrownBy(() -> HandlingReportParser.parseCompletionTime(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Completion time is required");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2022-01-02T03:04:05"
    })
    public void shouldReturnDateOnParsingValidCompletionTime(String input) {
        Date result = HandlingReportParser.parseCompletionTime(LocalDateTime.parse(input));
        assertThat(result).isNotNull();
    }
}