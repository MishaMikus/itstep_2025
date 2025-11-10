package task14;

import aqa_2025.day14.AllureListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;

import static aqa_2025.day14.Driver.driver;

@Listeners(AllureListener.class)
public class Task14Test {

    @BeforeTest
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @Test
    void task13Test() {
        driver.get("https://www.demoblaze.com/");

        scrollToBottom(driver,Duration.ofSeconds(10));
        waitPage();
        scrollAndClickNext();
    }

    @Test
    void task13FailTest() {
        driver.get("https://www.demoblaze.com/");

        scrollToBottom(driver,Duration.ofSeconds(10));
        waitPage();
        scrollAndClickNext();
        Assert.fail();
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
