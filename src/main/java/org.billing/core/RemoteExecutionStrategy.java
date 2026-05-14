package org.billing.core;

import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteExecutionStrategy implements WebdriverTestStrategy {

    @Override
    public WebDriver setupDriver(String browser) {
        switch (browser) {
            case "chrome":
                try {
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--incognito");
                    options.setCapability("browserName", "chrome");
                    return new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), options);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            case "firefox":
                return new FirefoxDriver();
            default:
                throw new IllegalArgumentException();
        }

    }
}
