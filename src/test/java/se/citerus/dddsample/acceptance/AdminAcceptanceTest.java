package se.citerus.dddsample.acceptance;

import org.junit.Test;
import se.citerus.dddsample.acceptance.pages.LaunchPage;

public class AdminAcceptanceTest extends AbstractAcceptanceTest {
    @Test
    public void adminSiteIsOperational() {
        LaunchPage home = new LaunchPage(driver,"http://localhost:8080/");
        home.goToAdminPage();
    }
}