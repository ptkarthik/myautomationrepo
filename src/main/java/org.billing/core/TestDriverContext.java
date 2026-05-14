package org.billing.core;

import org.openqa.selenium.WebDriver;

public class TestDriverContext {

    private WebdriverTestStrategy webdriverTestStrategy;

    public TestDriverContext(WebdriverTestStrategy webdriverTestStrategy) {
        this.webdriverTestStrategy = webdriverTestStrategy;
    }

    public WebDriver getWebdriver(String browser) {
        return webdriverTestStrategy.setupDriver(browser);
    }

    public void setWebdriverTestStrategy(WebdriverTestStrategy webdriverTestStrategy) {
        this.webdriverTestStrategy = webdriverTestStrategy;
    }


}
