package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class CargoBookingPage {

    private final WebDriver driver;
    private final int port;

    public CargoBookingPage(WebDriver driver, int port) {
        this.driver = driver;
        this.port = port;

        WebElement newCargoTableCaption = driver.findElement(By.cssSelector("table caption"));

        assertThat("Book new cargo").isEqualTo(newCargoTableCaption.getText());
    }

    public void selectOrigin(String origin) {
        Select select = new Select(driver.findElement(By.name("originUnlocode")));
        select.selectByVisibleText(origin);
    }

    public void selectDestination(String destination) {
        Select select = new Select(driver.findElement(By.name("destinationUnlocode")));
        select.selectByVisibleText(destination);
    }

    public CargoDetailsPage book() {
        driver.findElement(By.name("originUnlocode")).submit();

        return new CargoDetailsPage(driver, port);
    }

    public void selectArrivalDeadline(LocalDate arrivalDeadline) {
        WebElement datePicker = driver.findElement(By.id("arrivalDeadline"));
        datePicker.clear();
        datePicker.sendKeys(arrivalDeadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
