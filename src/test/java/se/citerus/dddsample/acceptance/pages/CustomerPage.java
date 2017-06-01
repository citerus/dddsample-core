package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomerPage {
    private final WebDriver driver;

    public CustomerPage(WebDriver driver) {
        this.driver = driver;
        assertEquals("Tracking cargo", driver.getTitle());
    }

    public void trackCargoWithIdOf(String trackingId) {
        WebElement element = driver.findElement(By.id("idInput"));
        element.sendKeys(trackingId);
        element.submit();

    }

    public void expectCargoLocation(String expectedLocation) {
        WebElement cargoSummary = driver.findElement(By.cssSelector("#result h2"));
        assertTrue(cargoSummary.getText().endsWith(expectedLocation));
    }
}
