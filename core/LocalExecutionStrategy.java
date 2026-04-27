package org.billing.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LocalExecutionStrategy implements WebdriverTestStrategy {

    @Override
    public WebDriver setupDriver(String browser) {
        switch (browser) {
            case "chrome":
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito");
                // options.addArguments("--headless");
                return new ChromeDriver(options);
            case "firefox":
                return new FirefoxDriver();
            default:
                throw new IllegalArgumentException();
        }

    }
}
