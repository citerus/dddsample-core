package se.citerus.dddsample.interfaces.handling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import se.citerus.dddsample.Application;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.infrastructure.sampledata.SampleLocations;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class HandlingReportIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private HandlingEventRepository repo;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Disabled //TODO investigate failure when not run in isolation
    @Transactional
    @Test
    void shouldReturn201ResponseWhenHandlingReportIsSubmitted() throws Exception {
        String body = mapper.writeValueAsString(ImmutableMap.of(
                "completionTime", "2022-10-30T13:37:00",
                "trackingIds", Collections.singletonList("ABC123"),
                "type", HandlingEvent.Type.CUSTOMS.name(),
                "unLocode", SampleLocations.DALLAS.unlocode
        ));
        URI uri = new UriTemplate("http://localhost:{port}/dddsample/handlingReport").expand(port);
        RequestEntity<String> request = RequestEntity
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);

        Thread.sleep(1000); // TODO replace with Awaitility

        HandlingHistory handlingHistory = repo.lookupHandlingHistoryOfCargo(new TrackingId("ABC123"));
        HandlingEvent handlingEvent = handlingHistory.mostRecentlyCompletedEvent();
        assertThat(handlingEvent.cargo().trackingId().idString()).isEqualTo("ABC123");
        assertThat(handlingEvent)
                .extracting("type", "location.unlocode")
                .containsExactly(HandlingEvent.Type.CUSTOMS, "USDAL");
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnValidationErrorResponseWhenInvalidHandlingReportIsSubmitted() throws Exception {
        String body = mapper.writeValueAsString(ImmutableMap.of(
                "completionTime", "invalid date",
                "trackingIds", Collections.singletonList("ABC123"),
                "type", HandlingEvent.Type.CUSTOMS.name(),
                "unLocode", SampleLocations.STOCKHOLM.unlocode,
                "voyageNumber", "0101"
        ));

        URI uri = new UriTemplate("http://localhost:{port}/dddsample/handlingReport").expand(port);
        RequestEntity<String> request = RequestEntity
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
        try {
            restTemplate.exchange(request, String.class);
            fail("Did not throw HttpClientErrorException");
        } catch (HttpClientErrorException e) {
            Map<String, String> map = mapper.readValue(e.getResponseBodyAsString(), Map.class);
            assertThat(map.get("message")).contains("JSON parse error: Cannot deserialize value of type `java.time.LocalDateTime` from String \"invalid date\": Text 'invalid date' could not be parsed at index 0");
        }
    }
}
