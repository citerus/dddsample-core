package se.citerus.dddsample.interfaces.tracking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import se.citerus.dddsample.infrastructure.persistence.inmemory.CargoRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.HandlingEventRepositoryInMem;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringJUnit4ClassRunner.class)
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
        assertEquals(trackingId, cargoTrackingViewAdapter.getTrackingId());
    }

    @Test
    public void cannnotGetUnknownCargo() throws Exception {
        String trackingId = "UNKNOWN";
        Map<String, Object> model = mockMvc.perform(post("/track").param("trackingId", trackingId)).andReturn().getModelAndView().getModel();
        Errors errors = (Errors) model.get(BindingResult.MODEL_KEY_PREFIX + "trackCommand");
        FieldError fe = errors.getFieldError("trackingId");
        assertEquals("cargo.unknown_id", fe.getCode());
        assertEquals(1, fe.getArguments().length);
        assertEquals(trackingId, fe.getArguments()[0]);
    }

}