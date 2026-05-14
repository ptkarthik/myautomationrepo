package org.billing.pages;

import lombok.Data;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Data
public class BrokerPage extends BasePage {
    @FindBy(xpath = "//a[contains(text(),'Brokers')]")
    public WebElement brokers;

    @FindBy(xpath = "//span[contains(text(),'Brokers')]/following::a[@title='Add New Broker']")
    public WebElement addBroker;

    @FindBy(xpath = "//div[@class='modal-content']")
    public WebElement editBrokerTab;

    @FindBy(xpath = "//div[@class='modal-content']/div/h5")
    public WebElement editBrokerTitle;

    @FindBy(id = "nameLabel")
    public WebElement editBrokerTabBrokerNameText;

    @FindBy(xpath = "//input[@id='name']")
    public WebElement brokerAddInputBox;

    @FindBy(xpath = "//button[contains(text(),'Save')]")
    public WebElement brokerSaveButton;

    @FindBy(xpath = "//button[contains(text(),'Cancel')]")
    public WebElement brokerCancelButton;

    @FindBy(xpath = "//input[@class='entity-search']")
    public WebElement brokerSearchBox;

    @FindBy(xpath = "//table[contains(@class,'entity')]")
    public WebElement brokerList;

    @FindBy(xpath = "//table[contains(@class,'entity')]//tbody/tr[1]/td[2]")
    public WebElement firstBrokerText;

    @FindBy(xpath = "//div[contains(@class,'delete')]/div[@class='modal-content']")
    public WebElement deleteWarningDailogBox;

    @FindBy(xpath = "//a[@title='Delete Broker']")
    public WebElement deleteFirstBrokerName;

    @FindBy(xpath = "//div[contains(text(),'delete broker')]/div/button[contains(text(),'Yes')]")
    public WebElement YesButtonOfDeleteBroker;

    @FindBy(xpath = "//div[contains(text(),'Are you sure you want to')]")
    public WebElement areYouSureToDeleteText;

    @FindBy(xpath = "//div[contains(text(),'delete broker')]/div/button[contains(text(),'No')]")
    public WebElement NoButtonOfDeleteBroker;

    @FindBy(id = "broker:brokerList:j_id_jsp_1056473468_39_table")
    public WebElement brokerListPagination;

    @FindBy(xpath = "(//table[contains(@id,'brokerList')])[2]//td[1]")
    public WebElement firstBrokerPage;

    @FindBy(xpath = "//td[contains(@onClick,'last')]")
    public WebElement lastBrokerPage;

    @FindBy(xpath = "//td[contains(@onClick,'fastforward')]")
    public WebElement nextBrokerPage;

    @FindBy(xpath = "(//table[contains(@id,'brokerList')])[2]//td[2]")
    public WebElement previousBrokerPage;

    @FindBy(xpath = "//tbody[@id='broker:brokerList:tb']/tr")
    public WebElement brokersAvailableAtBrokerList;

}
