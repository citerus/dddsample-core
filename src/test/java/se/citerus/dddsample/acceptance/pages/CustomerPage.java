package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerPage {
    private final WebDriver driver;

    public CustomerPage(WebDriver driver, int port) {
        this.driver = driver;
        driver.get(String.format("http://localhost:%d/dddsample/track", port));
        assertThat("Tracking cargo").isEqualTo(driver.getTitle());
    }

    public void trackCargoWithIdOf(String trackingId) {
        WebElement element = driver.findElement(By.id("idInput"));
        element.sendKeys(trackingId);
        element.submit();

    }

    public void expectCargoLocation(String expectedLocation) {
        WebElement cargoSummary = driver.findElement(By.cssSelector("#result h2"));
        assertThat(cargoSummary.getText()).endsWith(expectedLocation);
    }

    public void expectErrorFor(String expectedErrorMessage) {
        WebElement error = driver.findElement(By.cssSelector(".error"));
        assertThat(error.getText()).endsWith(expectedErrorMessage);
    }

    public void expectNotificationOf(String expectedNotificationMessage) {
        WebElement error = driver.findElement(By.cssSelector(".notify"));
        assertThat(error.getText()).endsWith(expectedNotificationMessage);
    }
}
