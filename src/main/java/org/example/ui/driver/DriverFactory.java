package org.example.ui.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import static org.example.config.ConfigProps.APP_PROPERTY;

public final class DriverFactory {
  private DriverFactory() {}

  public static WebDriver createDriver() {
    BrowserType browser = BrowserType.from(APP_PROPERTY.browser());
    boolean headless = APP_PROPERTY.headless();

    return switch (browser) {
      case CHROME -> createChrome(headless);
      case FIREFOX -> createFirefox(headless);
    };
  }

  private static WebDriver createChrome(boolean headless) {
    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();
    if (headless) {
      options.addArguments("--headless=new");
    }
    options.addArguments("--window-size=1920,1080");
    options.addArguments("--disable-gpu");
    options.addArguments("--no-sandbox");

    return WebDriverManager.chromedriver().capabilities(options).create();
  }

  private static WebDriver createFirefox(boolean headless) {
    WebDriverManager.firefoxdriver().setup();

    FirefoxOptions options = new FirefoxOptions();
    if (headless) {
      options.addArguments("-headless");
    }

    return WebDriverManager.firefoxdriver().capabilities(options).create();
  }

}
