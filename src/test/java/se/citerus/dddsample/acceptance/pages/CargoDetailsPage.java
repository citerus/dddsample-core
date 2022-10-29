package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class CargoDetailsPage {
    public static final String TRACKING_ID_HEADER = "Details for cargo ";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final WebDriver driver;
    private final int port;
    private String trackingId;

    public CargoDetailsPage(WebDriver driver, int port) {
        this.driver = driver;
        this.port = port;

        WebElement newCargoTableCaption = driver.findElement(By.cssSelector("table caption"));

        assertThat(newCargoTableCaption.getText()).startsWith(TRACKING_ID_HEADER);
        trackingId = newCargoTableCaption.getText().replaceFirst(TRACKING_ID_HEADER, "");
    }

    public String getTrackingId() {
        return trackingId;
    }

    public AdminPage listAllCargo() {
        driver.findElement(By.linkText("List all cargos")).click();

        return new AdminPage(driver, port);
    }

    public void expectOriginOf(String expectedOrigin) {
        String actualOrigin = driver.findElement(By.xpath("//div[@id='container']/table/tbody/tr[1]/td[2]")).getText();

        assertThat(expectedOrigin).isEqualTo(actualOrigin);
    }

    public void expectDestinationOf(String expectedDestination) {
        String actualDestination = driver.findElement(By.xpath("//div[@id='container']/table/tbody/tr[2]/td[2]")).getText();

        assertThat(expectedDestination).isEqualTo(actualDestination);
    }

    public CargoDestinationPage changeDestination() {
        driver.findElement(By.linkText("Change destination")).click();

        return new CargoDestinationPage(driver, port);
    }

    public void expectArrivalDeadlineOf(LocalDate expectedArrivalDeadline) {
        String actualArrivalDeadline = driver.findElement(By.xpath("//div[@id='container']/table/tbody/tr[4]/td[2]")).getText();

        assertThat(expectedArrivalDeadline.format(FORMATTER)).isEqualTo(actualArrivalDeadline);
    }
}
