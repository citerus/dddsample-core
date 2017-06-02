package se.citerus.dddsample.acceptance;

import org.junit.Test;
import se.citerus.dddsample.acceptance.pages.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static junit.framework.TestCase.assertTrue;

public class AdminAcceptanceTest extends AbstractAcceptanceTest {
    @Test
    public void adminSiteCargoListContainsCannedCargo() {
        LaunchPage home = new LaunchPage(driver,"http://localhost:8080/");
        AdminPage page = home.goToAdminPage();
        page.listAllCargo();

        assertTrue("Cargo list doesn't contain ABC123", page.listedCargoContains("ABC123"));
        assertTrue("Cargo list doesn't contain JKL567", page.listedCargoContains("JKL567"));
    }

    @Test
    public void adminSiteCanBookNewCargo() {
        LaunchPage home = new LaunchPage(driver,"http://localhost:8080/");
        AdminPage adminPage = home.goToAdminPage();
        CargoBookingPage cargoBookingPage = adminPage.bookNewCargo();
        cargoBookingPage.selectOrigin("NLRTM");
        cargoBookingPage.selectDestination("USDAL");
        LocalDate arrivalDeadline = LocalDate.now().plus(2, ChronoUnit.WEEKS);
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
//        cargoDetailsPage.expectArrivalDeadlineOf(arrivalDeadline);

    }
}