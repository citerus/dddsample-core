package se.citerus.dddsample.interfaces.tracking.ws;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import se.citerus.dddsample.Application;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CargoTrackingRestServiceIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    @Test
    void shouldReturn200ResponseAndJsonWhenRequestingCargoWithIdABC123() throws Exception {
        URI uri = new UriTemplate("http://localhost:{port}/dddsample/api/track/ABC123").expand(port);
        RequestEntity<Void> request = RequestEntity.get(uri).build();

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        String expected = StreamUtils.copyToString(getClass().getResourceAsStream("/sampleCargoTrackingResponse.json"), StandardCharsets.UTF_8);
        assertThat(response.getHeaders().get("Content-Type")).containsExactly("application/json;charset=UTF-8");
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void shouldReturnValidationErrorResponseWhenInvalidHandlingReportIsSubmitted() throws Exception {
        URI uri = new UriTemplate("http://localhost:{port}/dddsample/api/track/MISSING").expand(port);
        RequestEntity<Void> request = RequestEntity.get(uri).build();

        try {
            restTemplate.exchange(request, String.class);
            fail("Did not throw HttpClientErrorException");
        } catch (HttpClientErrorException e) {
            assertThat(e.getResponseHeaders().getLocation()).isEqualTo(new URI("/dddsample/api/track/MISSING"));
        }
    }
}
