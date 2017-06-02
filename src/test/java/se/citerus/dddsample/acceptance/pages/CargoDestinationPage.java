package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static junit.framework.TestCase.assertTrue;

public class CargoDestinationPage {
    private final WebDriver driver;

    public CargoDestinationPage(WebDriver driver) {
        this.driver = driver;
        WebElement cargoDestinationHeader = driver.findElement(By.cssSelector("table caption"));

        assertTrue(cargoDestinationHeader.getText().startsWith("Change destination for cargo "));
    }

    public CargoDetailsPage selectDestinationTo(String destination) {
        WebElement destinationPicker = driver.findElement(By.name("unlocode"));
        Select select = new Select(destinationPicker);
        select.selectByVisibleText(destination);

        destinationPicker.submit();

        return new CargoDetailsPage(driver);
    }
}
