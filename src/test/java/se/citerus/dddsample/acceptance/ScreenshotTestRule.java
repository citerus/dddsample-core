package se.citerus.dddsample.acceptance;

import org.apache.commons.io.FileUtils;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

class ScreenshotTestRule implements MethodRule {
    private WebDriver driver;

    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } catch (Throwable t) {
                    captureScreenshot(frameworkMethod.getName());
                    throw t;
                }
            }

            public void captureScreenshot(String fileName) {
                try {
                    new File("target/surefire-reports/").mkdirs(); // Insure directory is there
                    File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                    FileUtils.copyFile(scrFile, new File("target/surefire-reports/screenshot-" + fileName + ".png"));
                } catch (Exception e) {
                    System.err.println("ERROR: " + e);
                }
            }
        };
    }

    public void updateDriver(WebDriver driver) {
        this.driver = driver;
    }
}