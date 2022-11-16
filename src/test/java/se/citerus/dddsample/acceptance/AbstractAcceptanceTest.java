package se.citerus.dddsample.acceptance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;
import se.citerus.dddsample.Application;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.datasource.url=jdbc:hsqldb:mem:dddsample_acceptance_test"})
public abstract class AbstractAcceptanceTest {

    @Autowired
    private WebApplicationContext context;

    protected WebDriver driver;

    @LocalServerPort
    public int port;

    @BeforeEach
    public void setup() {
        driver = MockMvcHtmlUnitDriverBuilder.webAppContextSetup(context).contextPath("/dddsample").build();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
