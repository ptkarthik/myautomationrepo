package org.billing.core;

import org.openqa.selenium.WebDriver;

public interface WebdriverTestStrategy {

    WebDriver setupDriver(String browser);
}
