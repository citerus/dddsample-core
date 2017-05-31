package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;

public class CustomerPage {
    public CustomerPage(WebDriver driver) {
        assertEquals("Tracking cargo", driver.getTitle());
    }
}
