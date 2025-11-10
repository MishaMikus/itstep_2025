package task21;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.testng.annotations.Test;

import java.io.File;

public class Task21Test {
    @Test
    void calcTest() {
        Screen screen = new Screen();
        Pattern icon = new Pattern(
                new File("src/main/resources/pattern/all_app_ico.png")
                        .getAbsolutePath()).similar(0.7f);
        System.out.println("Looking for: " + icon.getFilename());
        try {
            screen.find(icon).click();
        } catch (FindFailed e) {
            throw new RuntimeException(e);
        }
    }
}
