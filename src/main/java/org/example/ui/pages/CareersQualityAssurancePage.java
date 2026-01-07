package org.example.ui.pages;

import io.qameta.allure.Step;
import org.example.ui.utils.Waits;
import org.openqa.selenium.By;

import static org.example.config.ConfigProps.APP_PROPERTY;

public class CareersQualityAssurancePage extends BasePage {

  private static final String URL = "/careers/quality-assurance/";

  private final By pageHeading = By.xpath("//h1[normalize-space()='Quality Assurance' or contains(normalize-space(), 'Quality Assurance')]");
  private final By seeAllQaJobsBtn = By.xpath("//a[normalize-space()='See all QA jobs' or contains(normalize-space(),'See all QA jobs')]");

  @Step("Open Careers Quality Assurance page")
  public CareersQualityAssurancePage open() {
    driver().get(APP_PROPERTY.baseUrl() + URL);
    acceptCookiesIfPresent();
    Waits.visible(driver(), pageHeading);
    return this;
  }

  @Step("Click See All Jobs button")
  public OpenPositionsPage clickSeeAllQaJobs() {
    Waits.clickable(driver(), seeAllQaJobsBtn).click();
    return new OpenPositionsPage();
  }
}
