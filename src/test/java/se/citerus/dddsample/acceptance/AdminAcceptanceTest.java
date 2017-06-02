package se.citerus.dddsample.acceptance;

import org.junit.Test;
import se.citerus.dddsample.acceptance.pages.AdminPage;
import se.citerus.dddsample.acceptance.pages.CargoBookingPage;
import se.citerus.dddsample.acceptance.pages.CargoDetailsPage;
import se.citerus.dddsample.acceptance.pages.LaunchPage;

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
        AdminPage page = home.goToAdminPage();
        CargoBookingPage cargoBookingPage = page.bookNewCargo();
        cargoBookingPage.selectOrigin("NLRTM");
        cargoBookingPage.selectDestination("USDAL");
        CargoDetailsPage cargoDetailsPage = cargoBookingPage.book();
        String newCargoTrackingId = cargoDetailsPage.getTrackingId();

        AdminPage adminPage = cargoDetailsPage.listAllCargo();
        assertTrue("Cargo list doesn't contain " + newCargoTrackingId, adminPage.listedCargoContains(newCargoTrackingId));
    }
}