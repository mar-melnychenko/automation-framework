package org.example.ui.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {
  private DriverManager() {}

  private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

  public static WebDriver get() {
    WebDriver driver = DRIVER.get();
    if (driver == null) {
      throw new IllegalStateException("WebDriver is not initialized for this thread");
    }
    return driver;
  }

  public static void set(WebDriver driver) {
    DRIVER.set(driver);
  }

  public static void quit() {
    WebDriver driver = DRIVER.get();
    if (driver != null) {
      driver.quit();
      DRIVER.remove();
    }
  }
}
