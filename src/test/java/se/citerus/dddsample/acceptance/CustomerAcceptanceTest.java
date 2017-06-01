package se.citerus.dddsample.acceptance;

import org.junit.Test;
import se.citerus.dddsample.acceptance.pages.CustomerPage;
import se.citerus.dddsample.acceptance.pages.LaunchPage;

public class CustomerAcceptanceTest extends AbstractAcceptanceTest {
    @Test
    public void customerSiteIsOperational() {
        LaunchPage home = new LaunchPage(driver,"http://localhost:8080/");

        CustomerPage customerPage = home.goToCustomerPage();
        customerPage.trackCargoWithIdOf("ABC123");
        customerPage.expectCargoLocation("New York");


    }

}