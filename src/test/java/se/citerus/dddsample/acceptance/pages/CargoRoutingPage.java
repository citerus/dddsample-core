package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CargoRoutingPage {
    private WebDriver driver;
    private int port;

    public CargoRoutingPage (WebDriver driver, int port) {
        this.driver = driver;
        this.port = port;

        WebElement routingHeader = driver.findElement(By.xpath("//h1"));
        assertThat(routingHeader.getText().equals("Cargo Booking and Routing"));
    }

    public void expectAtLeastOneRoute() {
        WebElement assignRouteCaption = driver.findElement(By.cssSelector("form table caption"));
        assertEquals("Route candidate 1", assignRouteCaption.getText());
    }

    public CargoDetailsPage assignCargoToFirstRoute() {
        WebElement assignCargoForm = driver.findElement(By.xpath("//div[@id='container']/form[1]"));
        assignCargoForm.submit();

        return new CargoDetailsPage(driver, port);
    }


}
