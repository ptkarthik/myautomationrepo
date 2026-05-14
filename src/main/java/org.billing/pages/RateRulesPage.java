package org.billing.pages;

import org.billing.core.Baseclass;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

public class RateRulesPage extends Baseclass {

    private static By client = By.xpath("//*[text()='Client']/../following-sibling::div/input");
    private static By endVolume = By.xpath("//*[text()='End Volume']/..");
    private static By broker = By.xpath("//*[text()='Broker']/../following-sibling::div/input");
    private static By market = By.xpath("//*[text()='Market']/../following-sibling::div/input");
    private static final By loader = By.xpath("//img[@src='/broker_invoice/images/load.gif']");

    public String getRateRuleValColumn(String client, String broker, String market) {
        getDriver().findElement(this.client).sendKeys(client);
        getDriver().findElement(this.market).sendKeys(Keys.TAB);
        waitUntilElementIsNotVisible(loader, 10);
        getDriver().findElement(this.broker).sendKeys(broker);
        getDriver().findElement(this.market).sendKeys(Keys.TAB);
        waitUntilElementIsNotVisible(loader, 10);
        getDriver().findElement(this.market).sendKeys(market);
        getDriver().findElement(this.market).sendKeys(Keys.TAB);
        waitUntilElementIsNotVisible(loader, 10);
        waitABit(5000);
        return getDriver().findElement(By.xpath(
                "(//span[text()='" + client + "']/parent::td/following-sibling::td/span[text()='" + broker + "']/ancestor::tr/td[contains(@id,'valueColumn')])[2]/span[1]")).getText();
    }


}
