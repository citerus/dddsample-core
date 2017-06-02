package se.citerus.dddsample.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;

public class LaunchPage {
    private final WebDriver driver;
    private final String baseUrl;

    public LaunchPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        driver.get(baseUrl);
        assertEquals("DDDSample", driver.getTitle());
    }

    public CustomerPage goToCustomerPage() {
        driver.findElement(By.linkText("cargo tracking")).click();

        return new CustomerPage(driver);
    }

    public AdminPage goToAdminPage() {
        driver.findElement(By.linkText("booking and routing")).click();

        return new AdminPage(driver);
    }
}
