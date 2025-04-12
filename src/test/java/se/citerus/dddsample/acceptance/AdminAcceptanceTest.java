package se.citerus.dddsample.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import se.citerus.dddsample.acceptance.pages.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AdminAcceptanceTest extends AbstractAcceptanceTest {

    @DirtiesContext
    @Test
    public void adminSiteCargoListContainsCannedCargo() {
        AdminPage page = new AdminPage(driver, port);
        page.listAllCargo();

        assertThat(page.listedCargoContains("ABC123")).isTrue()
                .withFailMessage("Cargo list doesn't contain ABC123");
        assertThat(page.listedCargoContains("JKL567")).isTrue()
                .withFailMessage("Cargo list doesn't contain JKL567");
    }

    @DirtiesContext
    @Test
    public void adminSiteCanBookNewCargo() {
        AdminPage adminPage = new AdminPage(driver, port);

        CargoBookingPage cargoBookingPage = adminPage.bookNewCargo();
        cargoBookingPage.selectOrigin("NLRTM");
        cargoBookingPage.selectDestination("USDAL");
        LocalDate arrivalDeadline = LocalDate.now().plus(3, ChronoUnit.WEEKS);
        cargoBookingPage.selectArrivalDeadline(arrivalDeadline);
        CargoDetailsPage cargoDetailsPage = cargoBookingPage.book();

        String newCargoTrackingId = cargoDetailsPage.getTrackingId();
        adminPage = cargoDetailsPage.listAllCargo();
        assertThat(adminPage.listedCargoContains(newCargoTrackingId)).isTrue()
                .withFailMessage("Cargo list doesn't contain %s", newCargoTrackingId);

        cargoDetailsPage = adminPage.showDetailsFor(newCargoTrackingId);
        cargoDetailsPage.expectOriginOf("NLRTM");
        cargoDetailsPage.expectDestinationOf("USDAL");

        CargoDestinationPage cargoDestinationPage = cargoDetailsPage.changeDestination();
        cargoDetailsPage = cargoDestinationPage.selectDestinationTo("AUMEL");
        cargoDetailsPage.expectDestinationOf("AUMEL");
        cargoDetailsPage.expectArrivalDeadlineOf(arrivalDeadline);

        // Route cargo
        cargoDetailsPage.expectRoutedOf("Not routed");
        CargoRoutingPage cargoRoutingPage = cargoDetailsPage.routeCargo();
        cargoRoutingPage.expectAtLeastOneRoute();
        CargoDetailsPage routedCargoDetailsPage = cargoRoutingPage.assignCargoToFirstRoute();
        routedCargoDetailsPage.expectItinerary();
    }
}
