package se.citerus.dddsample.acceptance;

import org.junit.Before;
import org.junit.Test;
import se.citerus.dddsample.acceptance.pages.CustomerPage;
import se.citerus.dddsample.acceptance.pages.LaunchPage;

public class CustomerAcceptanceTest extends AbstractAcceptanceTest {
    private CustomerPage customerPage;

    @Before
    public void goToCustomerPage() {
        LaunchPage home = new LaunchPage(driver,"http://localhost:8080/");
        customerPage = home.goToCustomerPage();
    }

    @Test
    public void customerSiteCanTrackValidCargo() {
        customerPage.trackCargoWithIdOf("ABC123");
        customerPage.expectCargoLocation("New York");
    }

    @Test
    public void customerSiteErrorsOnInvalidCargo() {
        customerPage.trackCargoWithIdOf("XXX999");
        customerPage.expectErrorFor("Unknown tracking id");
    }

    @Test
    public void customerSiteNotifiesOnMisdirectedCargo() {
        customerPage.trackCargoWithIdOf("JKL567");
        customerPage.expectNotificationOf("Cargo is misdirected");
    }

}