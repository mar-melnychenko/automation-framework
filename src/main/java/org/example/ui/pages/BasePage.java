package org.example.ui.pages;

import io.qameta.allure.Step;
import org.example.ui.driver.DriverManager;
import org.example.ui.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class BasePage {

  protected WebDriver driver() {
    return DriverManager.get();
  }

  @Step("Accept cookies")
  protected void acceptCookiesIfPresent() {
    By cookieBanner = By.xpath(
            "//*[contains(@class,'cookie') or contains(@id,'cookie') or contains(@class,'consent')]"
    );

    By acceptAllBtn = By.xpath(
            "//a[normalize-space()='Accept All' or contains(normalize-space(), 'Accept All')] | " +
                    "//button[normalize-space()='Accept All' or contains(normalize-space(), 'Accept All')]"
    );

    try {
      if (!driver().findElements(acceptAllBtn).isEmpty()) {
        WebElement btn = driver().findElements(acceptAllBtn).getFirst();
        if (btn.isDisplayed()) {
          btn.click();
        }
      }

      Waits.wait(driver())
              .ignoring(StaleElementReferenceException.class)
              .until(ExpectedConditions.invisibilityOfElementLocated(cookieBanner));

    } catch (Exception ignored) {
    }
  }

  protected void scrollIntoView(WebElement element) {
    try {
      ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    } catch (Exception ignored) {}
  }
}
