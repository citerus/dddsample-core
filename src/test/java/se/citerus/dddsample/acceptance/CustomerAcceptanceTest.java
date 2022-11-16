package se.citerus.dddsample.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import se.citerus.dddsample.acceptance.pages.CustomerPage;

public class CustomerAcceptanceTest extends AbstractAcceptanceTest {
    private CustomerPage customerPage;

    @BeforeEach
    public void goToCustomerPage() {
        customerPage = new CustomerPage(driver, port);
    }

    @DirtiesContext
    @Test
    public void customerSiteCanTrackValidCargo() {
        customerPage.trackCargoWithIdOf("ABC123");
        customerPage.expectCargoLocation("New York");
    }

    @DirtiesContext
    @Test
    public void customerSiteErrorsOnInvalidCargo() {
        customerPage.trackCargoWithIdOf("XXX999");
        customerPage.expectErrorFor("Unknown tracking id");
    }

    @DirtiesContext
    @Test
    public void customerSiteNotifiesOnMisdirectedCargo() {
        customerPage.trackCargoWithIdOf("JKL567");
        customerPage.expectNotificationOf("Cargo is misdirected");
    }

}
