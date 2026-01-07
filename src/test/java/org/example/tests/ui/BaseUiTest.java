package org.example.tests.ui;

import org.example.tests.listeners.AllureTestListener;
import org.example.ui.driver.DriverFactory;
import org.example.ui.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import static org.example.config.ConfigProps.APP_PROPERTY;

@Listeners({AllureTestListener.class})
public abstract class BaseUiTest {

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    WebDriver driver = DriverFactory.createDriver();
    DriverManager.set(driver);

    driver.manage().window().maximize();
    driver.get(APP_PROPERTY.baseUrl());
  }

  @AfterMethod(alwaysRun = true)
  public void tearDown() {
    DriverManager.quit();
  }
}
