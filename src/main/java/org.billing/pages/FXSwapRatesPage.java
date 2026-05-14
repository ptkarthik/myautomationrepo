package org.billing.pages;

import lombok.Data;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Data
public class FXSwapRatesPage extends BasePage {
    @FindBy(xpath = "//a[contains(text(),'Home')]")
    private WebElement homePageText;

    @FindBy(xpath = "//a[contains(text(),'FX')]")
    private WebElement fxSwapRatePage;

    // Page header: "Fx Swap Rates"
    @FindBy(xpath = "//h5[contains(text(),'Fx Swap Rates')]")
    public WebElement fxSwapRatesHeader;

    // Add New Rate Rule button
    @FindBy(xpath = "//img[@alt='Add New Rate Rule']")
    public WebElement addNewRateRuleButton;

    @FindBy(xpath = "//div[contains(text(),'delete this FX Swap')]/div/button[contains(text(),'Yes')]")
    public WebElement YesButtonOfDeleteFxSwap;

    // Add New Rate Rule Text button
    @FindBy(xpath = "//h5[contains(text(),'Add New Rate Rule')]")
    public WebElement addNewRateRuleTextButton;

    // Add New Rate Rule close button
    @FindBy(xpath = "//h5[contains(text(),'Add New Rate Rule')]/following::button[1]")
    public WebElement addNewRateRuleCloseButton;

    // The main table on the page
    @FindBy(xpath = "//h5[contains(text(),'Fx Swap Rates')]/following::table")
    public WebElement mainTable;

    // The main table on the page
    @FindBy(xpath = "//h5[contains(text(),'Fx Swap Rates')]/following::table/tbody")
    public WebElement mainTableRowsList;

    // The main table on the add new rate rule box
    @FindBy(xpath = "//h5[contains(text(),'Add New Rate Rule')]/ancestor::div[@class='modal-content']")
    public WebElement addRateRuleMainTable;

    // The Tenor Text on the add new rate rule box
    @FindBy(xpath = "//label[@id='tenorLabel']")
    public WebElement tenorTextOnAddNewRuleBox;

    // The lowerbound Text on the add new rate rule box
    @FindBy(xpath = "//label[@id='lowerBoundInDaysLabel']")
    public WebElement lowerBoundTextOnAddNewRuleBox;

    // The upper bound Text on the add new rate rule box
    @FindBy(xpath = "//label[@id='upperBoundInDaysLabel']")
    public WebElement upperBoundTextOnAddNewRuleBox;

    // The max upper bound Text on the add new rate rule box
    @FindBy(xpath = "//label[@for='maxUpperBound']")
    public WebElement maxUpperBoundTextOnAddNewRuleBox;

    // The max upper bound checkbox on the add new rate rule box
    @FindBy(xpath = "//input[@id='maxUpperBound']")
    public WebElement maxUpperBoundCheckBoxOnAddNewRuleBox;

    // The save button on rule box
    @FindBy(xpath = "//button[contains(text(),'Save')]")
    public WebElement saveOnAddNewRuleBox;

    // The cancel button on rule box
    @FindBy(xpath = "//button[contains(text(),'Cancel')]")
    public WebElement cancelOnAddNewRuleBox;

    // The revenue share Text on the add new rate rule box
    @FindBy(xpath = "//label[@id='revenueShareLabel']")
    public WebElement revenueShareTextOnAddNewRuleBox;

    // The Tenor Input Box
    @FindBy(id = "tenor")
    public WebElement tenorInputBox;

    // The lower bound Input Box
    @FindBy(id = "lowerBoundInDays")
    public WebElement lowerBoundInputBox;

    // The upper bound Input Box
    @FindBy(id = "upperBoundInDays")
    public WebElement upperBoundInputBox;

    // The upper bound Input Box
    @FindBy(id = "revenueShare")
    public WebElement revenueShareInputBox;

    // Table column header: "Action"
    @FindBy(xpath = "//tr[@class='entity-table-header']/th[1]")
    public WebElement actionColumnHeader;

    // Table column header: "Day Count"
    @FindBy(xpath = "//tr[@class='entity-table-header']/th[3]")
    public WebElement dayCountHeader;

    // Table column header: "Day Count"
    @FindBy(xpath = "//tr[@class='entity-table-header']/th[2]")
    public WebElement tenorHeader;

    // Table column header: "Rate"
    @FindBy(xpath = "//tr[@class='entity-table-header']/th[4]")
    public WebElement revenueShareHeader;

    // Pagination button: "<<"
    @FindBy(xpath = "//button[contains(text(),'<<')][2]")
    public WebElement paginationPrevButton;

    // Pagination button: "<<"
    @FindBy(xpath = "//button[contains(text(),'<<')][1]")
    public WebElement paginationfirstButton;

    // Pagination button: ">>"
    @FindBy(xpath = "//button[contains(text(),'>>')][1]")
    public WebElement paginationNextButton;

    // Pagination button: ">>"
    @FindBy(xpath = "//button[contains(text(),'>>')][2]")
    public WebElement paginationLastButton;

    @FindBy(xpath = "//div[@class='invalid-feedback pb-1'][1]")
    public WebElement invalidRateRuleTextOne;

    @FindBy(xpath = "//div[@class='invalid-feedback pb-1'][2]")
    public WebElement invalidRateRuleTextTwo;
}
