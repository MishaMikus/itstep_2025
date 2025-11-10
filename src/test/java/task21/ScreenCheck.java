package task21;
import org.sikuli.script.*;

public class ScreenCheck {
    public static void main(String[] args) {
        saveScreenshot();
    }

    public static void saveScreenshot() {
        try {
            new Screen().capture().save(".", "capture_" + System.currentTimeMillis() + ".png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
