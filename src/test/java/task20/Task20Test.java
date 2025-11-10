package task20;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.net.HttpURLConnection;

public class Task20Test {

    private static Process appiumProcess;
    private static boolean appiumStartedByTests = false;
    private static final int APPIUM_PORT = 4723;

    @BeforeSuite
    void ensureEmulatorRunning() throws Exception {
        String deviceId = "emulator-5554";
        String emulatorPath = System.getProperty("user.home") + "/Library/Android/sdk/emulator/emulator";
        List<String> avds = listAvailableAvds(emulatorPath);
        System.out.println("Available AVDs: " + (avds.isEmpty() ? "<none>" : avds));
        String avdName = "Pixel_3a_API_34"; // desired AVD
        if (!avds.contains(avdName) && !avds.isEmpty()) {
            System.out.println("Configured AVD not found. Using first available: " + avds.get(0));
            avdName = avds.get(0);
        }
        if (!isEmulatorRunning(deviceId)) {
            System.out.println("Starting emulator: " + avdName);
            new ProcessBuilder(emulatorPath, "-avd", avdName, "-netdelay", "none", "-netspeed", "full")
                    .redirectErrorStream(true)
                    .start();
        } else {
            System.out.println("Emulator already running: " + deviceId);
        }
        // Wait until emulator shows up in adb and is fully booted & responsive.
        waitForEmulatorReady(deviceId, Duration.ofMinutes(5));
        // Ensure Appium server is running & healthy.
        ensureAppiumServerRunning(Duration.ofSeconds(600));
    }

    @AfterSuite(alwaysRun = true)
    void tearDownAppium() {
        if (appiumStartedByTests && appiumProcess != null && appiumProcess.isAlive()) {
            System.out.println("Stopping Appium server started by tests.");
            appiumProcess.destroy();
        }
    }

    // --- Appium server helpers ---
    private void ensureAppiumServerRunning(Duration timeout) throws Exception {
        if (isAppiumServerRunning()) {
            System.out.println("Appium server already running on port " + APPIUM_PORT);
            return;
        }
        startAppiumServer();
        waitForAppiumHealthy(timeout);
    }

    private boolean isAppiumServerRunning() {
        try {
            URL url = new URL("http://127.0.0.1:4723/status");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(1500);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            return code == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private void startAppiumServer() throws IOException {
        System.out.println("Starting Appium server on port " + APPIUM_PORT + "...");
        // Requires 'appium' CLI in PATH (Node.js global install). If not present, this will throw.
        appiumProcess = new ProcessBuilder("appium", "--port", String.valueOf(APPIUM_PORT), "--base-path", "/")
                .redirectErrorStream(true)
                .start();
        appiumStartedByTests = true;
    }

    private void waitForAppiumHealthy(Duration timeout) throws Exception {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (isAppiumServerRunning()) {
                System.out.println("Appium server is healthy.");
                return;
            }
            Thread.sleep(1000);
        }
        throw new IllegalStateException("Appium server did not become healthy within " + timeout.toSeconds() + "s");
    }
    // --- End Appium server helpers ---

    private List<String> listAvailableAvds(String emulatorPath) {
        List<String> result = new ArrayList<>();
        try {
            Process p = new ProcessBuilder(emulatorPath, "-list-avds").start();
            String out = new String(p.getInputStream().readAllBytes()).trim();
            for (String line : out.split("\\R")) {
                if (!line.isBlank()) result.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Failed to list AVDs: " + e.getMessage());
        }
        return result;
    }

    private boolean isEmulatorRunning(String deviceId) throws IOException {
        Process p = new ProcessBuilder("adb", "devices").start();
        String out = new String(p.getInputStream().readAllBytes());
        return out.contains(deviceId);
    }

    // --- Added readiness verification helpers ---
    private void waitForEmulatorReady(String deviceId, Duration timeout) throws Exception {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        // 1. Wait until device appears in adb devices list
        while (System.currentTimeMillis() < deadline && !isEmulatorRunning(deviceId)) {
            Thread.sleep(2000);
        }
        if (!isEmulatorRunning(deviceId)) {
            throw new IllegalStateException("Emulator did not appear in adb within timeout: " + timeout);
        }
        System.out.println("Device listed by adb: " + deviceId);
        // 2. Wait for sys.boot_completed == 1
        waitForAdbProperty(deviceId, "sys.boot_completed", "1", deadline, 3000L);
        // 3. Wait for boot animation stopped
        waitForAdbProperty(deviceId, "init.svc.bootanim", "stopped", deadline, 3000L);
        // 4. Simple responsiveness check (shell command)
        execAdb(deviceId, "shell", "echo", "ping");
        System.out.println("Emulator responsive and fully booted.");
    }

    private void waitForAdbProperty(String deviceId, String prop, String expected, long deadlineMillis, long pollInterval) throws Exception {
        while (System.currentTimeMillis() < deadlineMillis) {
            String value = execAdb(deviceId, "shell", "getprop", prop).trim();
            if (expected.equals(value)) {
                System.out.println("Property " + prop + "=" + value);
                return;
            }
            Thread.sleep(pollInterval);
        }
        throw new IllegalStateException("Timeout waiting for property " + prop + " to become '" + expected + "'.");
    }

    private String execAdb(String deviceId, String... args) throws Exception {
        // Build full adb command with -s deviceId
        List<String> cmd = new ArrayList<>();
        cmd.add("adb");
        cmd.add("-s");
        cmd.add(deviceId);
        for (String a : args) cmd.add(a);
        Process p = new ProcessBuilder(cmd).start();
        byte[] outBytes = p.getInputStream().readAllBytes();
        byte[] errBytes = p.getErrorStream().readAllBytes();
        int exit = p.waitFor();
        String out = new String(outBytes);
        String err = new String(errBytes);
        if (exit != 0) {
            throw new IOException("ADB command failed (exit=" + exit + "): " + cmd + "\nSTDERR: " + err);
        }
        if (!err.isBlank()) {
            System.out.println("[adb stderr] " + err.trim());
        }
        return out;
    }
    // --- End helpers ---

    @Test
    void task20Test() throws MalformedURLException {
        File apkFile = new File("apk/ApiDemos-debug.apk");
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setDeviceName("emulator-5554")
                .setApp(apkFile.getAbsolutePath());
        AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:" + APPIUM_PORT), options);
        driver.findElement(By.xpath("//android.widget.TextView[@content-desc=\"App\"]")).click();
        driver.quit();
    }

    @Test
    void task20CalcTest() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setDeviceName("emulator-5554")
                .setAppPackage("com.google.android.contacts")
                .setAppActivity("com.android.contacts.activities.PeopleActivity")
                .setNoReset(true);
        AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:" + APPIUM_PORT), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        System.out.println("\uD83D\uDCD7 Contacts app opened successfully!");
        driver.findElement(By.id("Create contact")).click();

        try {
            driver.findElement(By.xpath("//android.widget.TextView[@text='Device']"));
        } catch (Exception ignored) {}

        WebElement nameField = driver.findElement(By.xpath("//android.widget.EditText[@text='Name']"));
        nameField.sendKeys("John Doe");

        WebElement phoneField = driver.findElement(By.xpath("//android.widget.EditText[@text='Phone']"));
        phoneField.sendKeys("1234567890");

        driver.findElement(By.linkText("Save")).click();

        boolean contactVisible = driver.findElements(By.xpath("//android.widget.TextView[@text='John Doe']")).size() > 0;
        System.out.println(contactVisible ? "\u2705 Contact added successfully!" : "\u274C Contact not found!");

        driver.quit();
    }
}
