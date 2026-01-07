package org.example.ui.pages;

import io.qameta.allure.Step;
import org.example.ui.pages.components.JobCard;
import org.example.ui.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class OpenPositionsPage extends BasePage {

  private final By heading = By.xpath("//*[contains(normalize-space(), 'All open positions') or contains(normalize-space(), 'Browse Open Positions')]");
  private final By noPositionsAvailable = By.xpath("//p[normalize-space(text())='No positions available']");
  private final By jobCards = By.xpath("//*[@id='career-position-list']//div[contains(@class,'position-list-item')]");
  private final By jobCardRoots = By.xpath("//div[@id='jobs-list']/div");

  public OpenPositionsPage waitUntilLoaded() {
    acceptCookiesIfPresent();
    Waits.visible(driver(), heading);
    Waits.visible(driver(), jobCards);
    return this;
  }

  @Step("Filter jobs list by location {0}")
  public OpenPositionsPage filterByLocation(String location) {
    waitUntilLoaded();
    clickAllLocationDropdown();
    selectLocation(location);
    waitForJobsListToUpdate();
    return this;
  }

  @Step("Filter jobs list by department {0}")
  public OpenPositionsPage filterByDepartment(String department) {
    waitUntilLoaded();
    clickAllDepartmentDropdown();
    selectDepartment(department);
    waitForJobsListToUpdate();
    return this;
  }

  public List<JobCard> jobCards() {
    By jobsList = By.id("jobs-list");
    Waits.visible(driver(), jobsList);
    Waits.wait(driver())
            .until(d -> !d.findElements(jobCardRoots).isEmpty());
    List<WebElement> roots = driver().findElements(jobCardRoots);
    return roots.stream()
            .map(root -> new JobCard(driver(), root))
            .collect(Collectors.toList());
  }

  public boolean hasJobs() {
    List<WebElement> elements = driver().findElements(noPositionsAvailable);
    return elements.stream().noneMatch(WebElement::isDisplayed);
  }

  private void waitForJobsListToUpdate() {
    Waits.wait(driver()).until(d -> {
      boolean hasJobs = d.findElements(jobCardRoots).stream().anyMatch(WebElement::isDisplayed);
      boolean hasEmptyState = d.findElements(noPositionsAvailable).stream().anyMatch(WebElement::isDisplayed);
      return hasJobs || hasEmptyState;
    });
  }

  private void clickAllLocationDropdown() {
    By dropDown = By.id("filter-by-location");
    WebElement el = Waits.clickable(driver(), dropDown);
    scrollIntoView(el);
    el.click();
  }

  private void clickAllDepartmentDropdown() {
    By dropDown = By.id("filter-by-department");
    WebElement el = Waits.clickable(driver(), dropDown);
    scrollIntoView(el);
    el.click();
  }

  private void selectLocation(String location) {
    Select select = new Select(driver().findElement(By.id("filter-by-location")));
    select.selectByVisibleText(location);

    Waits.wait(driver()).until(d -> {
      Select s = new Select(d.findElement(By.id("filter-by-location")));
      String selected = s.getFirstSelectedOption().getText().trim();
      return selected.equals(location);
    });
  }

  private void selectDepartment(String department) {
    Select select = new Select(driver().findElement(By.id("filter-by-department")));
    select.selectByVisibleText(department);

    Waits.wait(driver()).until(d -> {
      Select s = new Select(d.findElement(By.id("filter-by-department")));
      String selected = s.getFirstSelectedOption().getText().trim();
      return selected.equals(department);
    });
  }

}
