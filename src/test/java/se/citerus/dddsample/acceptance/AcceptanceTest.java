package se.citerus.dddsample.acceptance;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import static org.junit.Assert.assertEquals;

public class AcceptanceTest {
    private WebDriver driver;

    @Before
    public void setup() {
        driver = new PhantomJSDriver();
    }

    @Test
    public void customerSiteIsOperational() {
        driver.get("http://localhost:8080/dddsample");

        assertEquals("DDDSample", driver.getTitle());
        WebElement adminLink = driver.findElement(By.linkText("cargo tracking"));
        adminLink.click();

        assertEquals("Tracking cargo", driver.getTitle());
    }

    @Test
    public void adminSiteIsOperational() {
        driver.get("http://localhost:8080/dddsample");

        assertEquals("DDDSample", driver.getTitle());
        WebElement adminLink = driver.findElement(By.linkText("booking and routing"));
        adminLink.click();

        assertEquals("Cargo Administration", driver.getTitle());
    }

    @After
    public void tearDown() {
        driver.quit();

    }
}