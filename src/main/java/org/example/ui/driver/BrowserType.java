package org.example.ui.driver;

public enum BrowserType {
  CHROME,
  FIREFOX;

  public static BrowserType from(String value) {
    return switch (value.toLowerCase()) {
      case "chrome" -> CHROME;
      case "firefox" -> FIREFOX;
      default -> throw new IllegalArgumentException("Unsupported browser: " + value);
    };
  }
}
