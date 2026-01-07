package org.example.ui.pages.components;

import org.example.ui.pages.LeverApplicationPage;
import org.example.ui.utils.Windows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class JobCard {
  private final WebDriver driver;
  private final WebElement root;

  private final By title = By.xpath(".//div/p");
  private final By department = By.xpath(".//div/span");
  private final By location = By.xpath(".//div/div");
  private final By viewRoleBtn = By.xpath(".//a[contains(., 'View')]");

  public JobCard(WebDriver driver, WebElement root) {
    this.driver = driver;
    this.root = root;
  }

  public String positionText() {
    return root.findElement(title).getText().trim();
  }

  public String departmentText() {
    return root.findElement(department).getText().trim();
  }

  public String locationText() {
    return root.findElement(location).getText().trim();
  }

  public LeverApplicationPage clickViewRole() {
    String original = Windows.currentHandle(driver);
    root.findElement(viewRoleBtn).click();
    Windows.waitForNewWindowAndSwitch(driver, original);
    return new LeverApplicationPage(driver);
  }
}
