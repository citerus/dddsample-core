package se.citerus.dddsample.interfaces.tracking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import se.citerus.dddsample.infrastructure.persistence.inmemory.CargoRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.HandlingEventRepositoryInMem;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = CargoTrackingControllerTest.TestConfiguration.class)
public class CargoTrackingControllerTest {
    public static class TestConfiguration {

    }

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        CargoRepositoryInMem cargoRepository = new CargoRepositoryInMem();
        cargoRepository.setHandlingEventRepository(new HandlingEventRepositoryInMem());
        cargoRepository.init();

        CargoTrackingController controller = new CargoTrackingController();
        controller.setCargoRepository(cargoRepository);
        controller.setHandlingEventRepository(new HandlingEventRepositoryInMem());

        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/jsp/");
        resolver.setSuffix(".jsp");
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(resolver).build();
    }

    @Test
    public void canGetCargo() throws Exception {
        String trackingId = "ABC";
        Map<String, Object> model = mockMvc.perform(post("/track").param("trackingId", trackingId)).andReturn().getModelAndView().getModel();
        CargoTrackingViewAdapter cargoTrackingViewAdapter = (CargoTrackingViewAdapter) model.get("cargo");
        assertThat(cargoTrackingViewAdapter.getTrackingId()).isEqualTo(trackingId);
    }

    @Test
    public void cannnotGetUnknownCargo() throws Exception {
        String trackingId = "UNKNOWN";
        Map<String, Object> model = mockMvc.perform(post("/track").param("trackingId", trackingId)).andReturn().getModelAndView().getModel();
        Errors errors = (Errors) model.get(BindingResult.MODEL_KEY_PREFIX + "trackCommand");
        FieldError fe = errors.getFieldError("trackingId");
        assertThat(fe.getCode()).isEqualTo("cargo.unknown_id");
        assertThat(fe.getArguments().length).isEqualTo(1);
        assertThat(fe.getArguments()[0]).isEqualTo(trackingId);
    }

}