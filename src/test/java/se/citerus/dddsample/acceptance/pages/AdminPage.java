package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminPage {
    private final WebDriver driver;
    private final int port;

    public AdminPage(WebDriver driver, int port) {
        this.driver = driver;
        this.port = port;
        driver.get(String.format("http://localhost:%d/dddsample/admin/list", port));
        assertThat("Cargo Administration").isEqualTo(driver.getTitle());
    }

    public void listAllCargo() {
        driver.findElement(By.linkText("List all cargos")).click();
        assertThat("Cargo Administration").isEqualTo(driver.getTitle());
    }

    public CargoBookingPage bookNewCargo() {
        driver.findElement(By.linkText("Book new cargo")).click();

        return new CargoBookingPage(driver, port);
    }

    public boolean listedCargoContains(String expectedTrackingId) {
        List<WebElement> cargoList = driver.findElements(By.cssSelector("#body table tbody tr td a"));
        Optional<WebElement> matchingCargo = cargoList.stream().filter(cargo -> cargo.getText().equals(expectedTrackingId)).findFirst();
        return matchingCargo.isPresent();
    }

    public CargoDetailsPage showDetailsFor(String cargoTrackingId) {
        driver.findElement(By.linkText(cargoTrackingId)).click();

        return new CargoDetailsPage(driver, port);
    }
}
