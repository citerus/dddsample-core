package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

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

    public void expectOriginOf(String expectedOrigin) {
        String actualOrigin = driver.findElement(By.xpath("//div[@id='container']/table/tbody/tr[1]/td[2]")).getText();

        assertEquals(expectedOrigin, actualOrigin);
    }

    public void expectDestinationOf(String expectedDestination) {
        String actualDestination = driver.findElement(By.xpath("//div[@id='container']/table/tbody/tr[2]/td[2]")).getText();

        assertEquals(expectedDestination, actualDestination);
    }

    public CargoDestinationPage changeDestination() {
        driver.findElement(By.linkText("Change destination")).click();

        return new CargoDestinationPage(driver);
    }

    public void expectArrivalDeadlineOf(LocalDate expectedArrivalDeadline) {
        String actualArrivalDeadline = driver.findElement(By.xpath("//div[@id='container']/table/tbody/tr[4]/td[2]")).getText();

        assertEquals(expectedArrivalDeadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), actualArrivalDeadline);
    }
}
