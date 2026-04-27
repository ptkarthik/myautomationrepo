package org.billing.pages;

import org.billing.core.Baseclass;
import org.openqa.selenium.By;

public class CapRulesPage extends Baseclass {

    public String getCapRuleValue(String client, String broker) {
        return getDriver().findElement(By.xpath(
                "//span[text()='" + client + "']/parent::td/following-sibling::td/span[text()='" + broker + "']/ancestor::tr/td[contains(@id,'valueColumn')]")).getText();
    }

    public void capRuleAction(String client, String broker, String action) {
        clickElement(By.xpath(
                "//span[text()='" + client + "']/parent::td/following-sibling::td/span[text()='" + broker + "']/ancestor::tr//img[contains(@src,'" + action + "')]"), 10);
    }


}
