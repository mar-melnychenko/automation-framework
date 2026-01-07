package org.example.ui.pages;

import io.qameta.allure.Step;
import org.example.ui.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LeverApplicationPage extends BasePage {

  private final WebDriver driver;

  public LeverApplicationPage(WebDriver driver) {
    this.driver = driver;
  }

  @Step("Is Job details page opened: {0}")
  public boolean isOpened(String jobTitle) {
    Waits.urlContains(driver(), "jobs.lever.co");
    By submitApplicationHeading = By.xpath(String.format("//h2[normalize-space()='%s']", jobTitle));
    Waits.visible(driver, submitApplicationHeading);
    return true;
  }
}
