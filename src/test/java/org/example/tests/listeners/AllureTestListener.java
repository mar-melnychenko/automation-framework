package org.example.tests.listeners;

import io.qameta.allure.Allure;
import org.example.ui.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.util.List;

public class AllureTestListener implements IInvokedMethodListener {

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    if (!method.isTestMethod() || testResult.isSuccess()) return;

    WebDriver driver;
    try {
      driver = DriverManager.get();
    } catch (Exception e) {
      return;
    }

    if (!isSessionAlive(driver)) return;

    attachScreenshot(testResult.getName(), driver);
    attachBrowserConsoleLogs(driver);
    attachThrowable(testResult.getThrowable());
  }

  private boolean isSessionAlive(WebDriver driver) {
    try {
      if (driver instanceof RemoteWebDriver rwd) {
        return rwd.getSessionId() != null;
      }
      driver.getTitle();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private void attachScreenshot(String testName, WebDriver driver) {
    try {
      byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      Allure.addAttachment(testName + " - Screenshot", "image/png",
              new ByteArrayInputStream(bytes), "png");
    } catch (Exception ignored) {}
  }

  private void attachBrowserConsoleLogs(WebDriver driver) {
    try {
      LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
      List<LogEntry> entries = logs.getAll();
      if (entries == null || entries.isEmpty()) return;

      StringBuilder sb = new StringBuilder();
      for (LogEntry e : entries) {
        sb.append(e.getLevel()).append(" ")
                .append(e.getTimestamp()).append(" ")
                .append(e.getMessage()).append("\n");
      }

      Allure.addAttachment("Browser console logs", "text/plain", sb.toString(), "txt");
    } catch (Exception ignored) {
    }
  }

  private void attachThrowable(Throwable t) {
    if (t == null) return;
    Allure.addAttachment("Failure", "text/plain", t.toString(), "txt");
  }
}
