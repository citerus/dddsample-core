package se.citerus.dddsample.acceptance;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

import se.citerus.dddsample.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractAcceptanceTest {

    @Autowired
    private WebApplicationContext context;

    protected WebDriver driver;

    @LocalServerPort
    public int port;

    @Before
    public void setup() {
        driver = MockMvcHtmlUnitDriverBuilder.webAppContextSetup(context).contextPath("/dddsample").build();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
