package org.billing.pages;

import org.billing.core.Baseclass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class BrokerInvoicePage extends Baseclass {


    private static final By BROKER_FILTER = By.id("invoices_page:filterText");
    private static final By VERSIONS = By.xpath("//*[@class='version-column' and contains(text(),'Version')]");
    private static final By REPORTS_TABLE_ROWS = By.id("//table[@id='invoices_page:invoicesTable']/tbody/tr[@class='dr-table-firstrow rich-table-firstrow']");
    private static final By BILLING_METHOD = By.id("mainForm:billingMethod");


    public void selectBillingMethod(String billingMethod) {
        waitUntilElementIsVisible(BILLING_METHOD, 10);
        selectOptionFromDropdown(BILLING_METHOD, billingMethod);
    }

    private void selectOptionFromDropdown(By locator, String option) {
        WebElement dropdown = getDriver().findElement(locator);
        Select select = new Select(dropdown);
        select.selectByVisibleText(option);
    }

    public void filterByBroker(String broker) {
        waitUntilElementIsVisible(BROKER_FILTER, 10);
        getDriver().findElement(BROKER_FILTER).sendKeys(broker);
    }

    public void verifyBrokerInSearchResult(String broker) {
        List<WebElement> rows = getDriver().findElements(REPORTS_TABLE_ROWS);
        boolean brokerFound = false;

        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.xpath("//span[@style='font-weight:bold']"));
            for (WebElement column : columns) {
                if (!column.getText().contains(broker)) {
                    brokerFound = true;
                    break;
                }
            }
            if (brokerFound) {
                break;
            }
        }

        if (brokerFound) {
            throw new AssertionError("Broker " + broker + " not found in the search results.");
        }
        clickElement(By.xpath("//span[text()='" + broker + "']/..//img[@src='/broker_invoice/images/plus.png']"), 10);
        waitABit(2000);
    }

    public List<WebElement> verifyNumberOfVersions(String broker) {
        List<WebElement> versions = getDriver().findElements(VERSIONS);
        return versions;
    }

    public void generateNewBuild(String broker) {
        clickElement(By.xpath("//span[text()='" + broker + "']/..//img[@src='/broker_invoice/images/rebuild.png']"), 10);
        waitABit(5000);
    }

    public void generateNewFastInvoiceBuild(String broker) {
        clickElement(By.xpath("//span[text()='" + broker + "']/..//img[@src='/broker_invoice/images/fastinvoice.png']"), 10);
        waitUntilElementIsNotVisible(By.xpath("//span[text()='" + broker + "']/..//img[@src='/broker_invoice/images/progress_indicator.gif' and contains(@id,'progressIndicatorFast')"), 30);
    }

    public void verifyNewVersionGenerated(String broker, int previousVersionCount) {
        waitABit(3000);
        List<WebElement> newVersionsCount = verifyNumberOfVersions(broker);
        if (newVersionsCount.size() != previousVersionCount + 1) {
            throw new AssertionError("New version not generated for broker ");
        }
    }
}
