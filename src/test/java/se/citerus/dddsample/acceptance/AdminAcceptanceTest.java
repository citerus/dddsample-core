package se.citerus.dddsample.acceptance;

import org.junit.Test;
import se.citerus.dddsample.acceptance.pages.AdminPage;
import se.citerus.dddsample.acceptance.pages.CargoBookingPage;
import se.citerus.dddsample.acceptance.pages.CargoDestinationPage;
import se.citerus.dddsample.acceptance.pages.CargoDetailsPage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static junit.framework.TestCase.assertTrue;

public class AdminAcceptanceTest extends AbstractAcceptanceTest {

    @Test
    public void adminSiteCargoListContainsCannedCargo() {
        AdminPage page = new AdminPage(driver);
        page.listAllCargo();

        assertTrue("Cargo list doesn't contain ABC123", page.listedCargoContains("ABC123"));
        assertTrue("Cargo list doesn't contain JKL567", page.listedCargoContains("JKL567"));
    }

    @Test
    public void adminSiteCanBookNewCargo() {
        AdminPage adminPage = new AdminPage(driver);

        CargoBookingPage cargoBookingPage = adminPage.bookNewCargo();
        cargoBookingPage.selectOrigin("NLRTM");
        cargoBookingPage.selectDestination("USDAL");
        LocalDate arrivalDeadline = LocalDate.now().plus(3, ChronoUnit.WEEKS);
        cargoBookingPage.selectArrivalDeadline(arrivalDeadline);
        CargoDetailsPage cargoDetailsPage = cargoBookingPage.book();

        String newCargoTrackingId = cargoDetailsPage.getTrackingId();
        adminPage = cargoDetailsPage.listAllCargo();
        assertTrue("Cargo list doesn't contain " + newCargoTrackingId, adminPage.listedCargoContains(newCargoTrackingId));

        cargoDetailsPage = adminPage.showDetailsFor(newCargoTrackingId);
        cargoDetailsPage.expectOriginOf("NLRTM");
        cargoDetailsPage.expectDestinationOf("USDAL");

        CargoDestinationPage cargoDestinationPage = cargoDetailsPage.changeDestination();
        cargoDetailsPage = cargoDestinationPage.selectDestinationTo("AUMEL");
        cargoDetailsPage.expectDestinationOf("AUMEL");
        cargoDetailsPage.expectArrivalDeadlineOf(arrivalDeadline);

    }
}
