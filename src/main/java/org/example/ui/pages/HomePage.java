package org.example.ui.pages;

import io.qameta.allure.Step;
import org.example.ui.utils.Waits;
import org.openqa.selenium.By;

import static org.example.config.ConfigProps.APP_PROPERTY;

public class HomePage extends BasePage {

  private final By heroHeading = By.xpath("//h1[contains(normalize-space(), 'Be unstoppable')]");
  private final By trustedByBlock = By.xpath("//*[contains(normalize-space(), 'TRUSTED BY') and contains(normalize-space(), 'CUSTOMERS')]");
  private final By threePromisesBlock = By.xpath("//*[contains(normalize-space(), 'The three promises')]");
  private final By getDemoCta = By.xpath("//a[contains(., 'Get a demo') or contains(., 'Get a Demo')]");

  @Step("Open Home page")
  public HomePage open() {
    driver().get(APP_PROPERTY.baseUrl());
    acceptCookiesIfPresent();
    return this;
  }

  public boolean isOpenedAndMainBlocksLoaded() {
    Waits.visible(driver(), heroHeading);
    Waits.visible(driver(), getDemoCta);
    Waits.visible(driver(), trustedByBlock);
    Waits.visible(driver(), threePromisesBlock);
    return true;
  }
}
