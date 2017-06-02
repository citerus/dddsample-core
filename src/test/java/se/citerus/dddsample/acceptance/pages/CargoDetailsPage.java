package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static junit.framework.TestCase.assertTrue;

public class CargoDetailsPage {
    public static final String TRACKING_ID_HEADER = "Details for cargo ";
    private final WebDriver driver;
    private String trackingId;

    public CargoDetailsPage(WebDriver driver) {
        this.driver = driver;

        WebElement newCargoTableCaption = driver.findElement(By.cssSelector("table caption"));

        assertTrue(newCargoTableCaption.getText().startsWith(TRACKING_ID_HEADER));
        trackingId = newCargoTableCaption.getText().replaceFirst(TRACKING_ID_HEADER, "");
    }

    public String getTrackingId() {
        return trackingId;
    }

    public AdminPage listAllCargo() {
        driver.findElement(By.linkText("List all cargos")).click();

        return new AdminPage(driver);
    }
}
