package org.example.ui.utils;

import org.openqa.selenium.WebDriver;

import java.util.Set;

public final class Windows {
    private Windows() {}

    public static String currentHandle(WebDriver driver) {
        return driver.getWindowHandle();
    }

    public static void waitForNewWindowAndSwitch(WebDriver driver, String originalHandle) {
        Waits.wait(driver).until(d -> d.getWindowHandles().size() > 1);

        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            if (!h.equals(originalHandle)) {
                driver.switchTo().window(h);
                return;
            }
        }
        throw new IllegalStateException("New window handle not found");
    }

}
