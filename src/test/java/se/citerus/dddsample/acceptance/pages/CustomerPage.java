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
        driver.get("http://localhost:8080/dddsample/track");
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

    public void expectErrorFor(String expectedErrorMessage) {
        WebElement error = driver.findElement(By.cssSelector(".error"));
        assertTrue(error.getText().endsWith(expectedErrorMessage));
    }

    public void expectNotificationOf(String expectedNotificationMessage) {
        WebElement error = driver.findElement(By.cssSelector(".notify"));
        assertTrue(error.getText().endsWith(expectedNotificationMessage));
    }
}
