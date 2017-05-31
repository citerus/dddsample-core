package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;

public class AdminPage {
    public AdminPage(WebDriver driver) {
        assertEquals("Cargo Administration", driver.getTitle());
    }
}
