package org.example.ui.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class Waits {
  private Waits() {}

  public static WebDriverWait wait(WebDriver driver) {
    return new WebDriverWait(driver, Duration.ofSeconds(15));
  }

  public static WebElement visible(WebDriver driver, By locator) {
    return wait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  public static WebElement clickable(WebDriver driver, By locator) {
    return wait(driver).until(ExpectedConditions.elementToBeClickable(locator));
  }

  public static boolean urlContains(WebDriver driver, String part) {
    return wait(driver).until(ExpectedConditions.urlContains(part));
  }
}
