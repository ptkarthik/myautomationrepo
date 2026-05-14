package org.billing.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Baseclass {
    private static final Logger logger = LogManager.getLogger(Baseclass.class);
    private static final ThreadLocal<WebDriver> threadLocal_driver = new ThreadLocal<WebDriver>();
    protected static final Properties prop = new Properties();

    private TestDriverContext context;
    private WebDriverWait wait;

    public Properties readConfigFile() {
        try {
            prop.load(new FileInputStream("config.properties"));
            logger.info("The Property files have been loaded");
        } catch (IOException e) {
            logger.error("IO Exception :: " + e.getMessage());
        }
        return prop;
    }

    public void setDriver(String strategy, String browser) {
        if (strategy.equalsIgnoreCase("local")) {
            context = new TestDriverContext(new LocalExecutionStrategy());
        } else if (strategy.equalsIgnoreCase("remote")) {
            context = new TestDriverContext(new RemoteExecutionStrategy());
        }
        threadLocal_driver.set(context.getWebdriver(browser));
    }

    public static WebDriver getDriver() {
        return threadLocal_driver.get();
    }

    public void waitABit(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean waitUntilElementIsNotVisible(WebElement element, int seconds) {
        try {
            return new WebDriverWait(getDriver(), Duration.ofSeconds(seconds)).until(ExpectedConditions.invisibilityOf(
                    element));
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public boolean waitUntilElementIsVisible(WebElement element, int seconds) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(seconds))
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (NoSuchElementException | TimeoutException | NullPointerException e) {
            return false;
        }
    }

    public boolean waitUntilElementIsVisible(By element, int seconds) {
        try {
            new WebDriverWait(getDriver(),
                    Duration.ofSeconds(seconds)).until(ExpectedConditions.visibilityOfElementLocated(element));
            return true;
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public boolean waitUntilElementIsNotVisible(By element, int seconds) {
        try {
            new WebDriverWait(getDriver(),
                    Duration.ofSeconds(seconds)).until(ExpectedConditions.invisibilityOfElementLocated(element));
            return true;
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public boolean waitUntilElementIsClickable(WebElement element, int seconds) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(seconds)).until(ExpectedConditions.elementToBeClickable(
                    element));
            return true;
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public boolean waitUntilElementIsClickable(By element, int seconds) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(seconds)).until(ExpectedConditions.elementToBeClickable(
                    element));
            return true;
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public void clickElement(WebElement element, int seconds) {
        waitUntilElementIsClickable(element, seconds);
        element.click();
    }

    public void clickElement(By element, int seconds) {
        waitUntilElementIsClickable(element, seconds);
        getDriver().findElement(element).click();
    }

    public void waitUntilElementIsVisibleAndClickElement(WebElement element, int seconds) {
        waitUntilElementIsVisible(element, seconds);
        element.click();
    }

    public void selectByVisibleText(By we, String text) {
        Select select = new Select(getDriver().findElement(we));
        select.selectByVisibleText(text);
    }

}
