package task13;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
public class Task13Test {
    protected final Logger LOGGER = LogManager.getLogger(this.getClass());
    WebDriver driver;

    @BeforeTest
    void setup() {
        LOGGER.info("Setting up driver");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
    }

    @Test
    void task13Test() {
        LOGGER.info("start task13Test" );
        driver.get("https://www.demoblaze.com/");

        scrollToBottom(driver,Duration.ofSeconds(10));
        waitPage();
        scrollAndClickNext();
    }

    public static void scrollToBottom(WebDriver driver, Duration timeout) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
        long startTime = System.currentTimeMillis();
        while (true) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            try {
                long finalLastHeight = lastHeight;
               shortWait.until(webDriver -> {
                    long newHeight = (long) js.executeScript("return document.body.scrollHeight");
                    return newHeight > finalLastHeight;
                });
            } catch (TimeoutException e) {
                break;
            }
            lastHeight = (long) js.executeScript("return document.body.scrollHeight");
            if (System.currentTimeMillis() - startTime > timeout.toMillis()) {
                System.out.println("Stopped scrolling: timeout reached.");
                break;
            }
        }
    }



    private void scrollAndClickNext() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.='Next']")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", nextButton);
        nextButton.click();
    }

    private void waitPage() {
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );

    }

    @AfterTest
    void teardown() {
        driver.quit();
    }

}
