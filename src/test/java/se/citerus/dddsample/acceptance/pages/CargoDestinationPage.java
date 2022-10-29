package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.assertj.core.api.Assertions.assertThat;

public class CargoDestinationPage {
    private final WebDriver driver;
    private final int port;

    public CargoDestinationPage(WebDriver driver, int port) {
        this.driver = driver;
        this.port = port;
        WebElement cargoDestinationHeader = driver.findElement(By.cssSelector("table caption"));

        assertThat(cargoDestinationHeader.getText()).startsWith("Change destination for cargo ");
    }

    public CargoDetailsPage selectDestinationTo(String destination) {
        WebElement destinationPicker = driver.findElement(By.name("unlocode"));
        Select select = new Select(destinationPicker);
        select.selectByVisibleText(destination);

        destinationPicker.submit();

        CargoDetailsPage cargoDetailsPage = new CargoDetailsPage(driver, port);
        return cargoDetailsPage;
    }
}
