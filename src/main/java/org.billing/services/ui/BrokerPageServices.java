package org.billing.services.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.endpoints.EndPoints;
import org.billing.api.responses.post.BrokerResponse;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.core.Baseclass;
import org.billing.pages.BrokerPage;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.propconfig.PropertiesReader;
import org.billing.validation.BaseValidation;
import org.billing.validation.GeneralValidations;
import org.billing.validation.ValidationUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BrokerPageServices extends BaseServices {
    private static final Logger logger = LogManager.getLogger(BrokerPageServices.class);
    SoftAssert softAssert = SoftAssertContainer.getInstance();
    BrokerPage brokerPage = new BrokerPage();
    BaseValidation brokerPageValidation = new GeneralValidations();
    // Locator for broker table rows
    private By brokerListLocator = By.xpath("//tbody[@id='broker:brokerList:tb']/tr");
    private By nextButton = By.xpath("(//table[contains(@id,'brokerList')])[2]//td[13]");
    public String countOfBrokers;

    /**
     * Opens the Broker Page.
     */
    public void openBrokerPage() {
        try {
            brokerPage.getBrokers().click();
            logger.info("The Broker Page is opened successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while opening the Broker Page.", e);
        }
    }

    /**
     * Clicks the Add Broker button.
     */
    public void clickAddBroker() {
        try {
            ((JavascriptExecutor) Baseclass.getDriver()).executeScript("arguments[0].click();", brokerPage.getAddBroker());
            logger.info("The Add Broker page icon is clicked successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while clicking the Add Broker page icon.", e);
        }
    }

    /**
     * Validates fields and UI elements on the Add Broker page.
     */
    public void validateAddBrokerTab() {
        try {
            brokerPageValidation.validateDisplayed(brokerPage.getEditBrokerTab(), "Add Broker Tabs");
            brokerPageValidation.validateEnabled(brokerPage.getBrokerAddInputBox(), "Broker Input Box");
            brokerPageValidation.validateText(brokerPage.getEditBrokerTitle(), "Add new broker");
            brokerPageValidation.validateText(brokerPage.getEditBrokerTabBrokerNameText(), "Broker:");
            brokerPageValidation.validateDisplayed(brokerPage.getBrokerSaveButton(), "Save Button");
            brokerPageValidation.validateDisplayed(brokerPage.getBrokerCancelButton(), "Cancel Button");
            logger.info("All validations for the Add Broker Tab are successful.");
        } catch (Exception e) {
            logger.error("Error occurred while validating the Add Broker Tab.", e);
        }
    }

    /**
     * Enters input text into the Add Broker input box.
     */
    public void enterInputText(String text) {
        try {
            brokerPage.getBrokerAddInputBox().sendKeys(text);
            logger.info("The Broker name inputted is: '{}'", text);
        } catch (Exception e) {
            logger.error("Error occurred while entering input text in the Broker input box.", e);
        }
    }

    public static List<BrokerResponse> fetchAllBrokers() throws Exception {
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
        Response response = APIUtils.getAllWithAuth(
                PropertiesReader.getProperty("baseUrl"),
                EndPoints.GET_ALL_BROKER,
                               headers
        );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.asString(),
                new TypeReference<List<BrokerResponse>>() {});
    }

    /**
     * Clicks the Save Broker button.
     */
    public void saveTheBroker() {
        try {
            ValidationUtils.waitForVisibility(brokerPage.getBrokerSaveButton());
            ((JavascriptExecutor) Baseclass.getDriver()).executeScript("arguments[0].click();", brokerPage.getBrokerSaveButton());
            logger.info("The Save button is clicked successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while clicking the Save button.", e);
        }
    }

    /**
     * Deletes a broker with the given broker name.
     */
    public void userDeletestheBrokerBack(String brokerName) {
        deleteTheBroker(brokerName);
    }

    /**
     * Validates whether the broker entered in the search box is available.
     */
    public void validateTheInputtedBrokerBySearch(String brokerName) throws InterruptedException {
        try {
            ValidationUtils.isElementDisplayed(brokerPage.getBrokerList());
            ValidationUtils.isElementDisplayed(brokerPage.getBrokerSearchBox());
            By searchBoxLocator = By.xpath("//input[@class='entity-search']");
            WebElement searchBoxWebelement = Baseclass.getDriver().findElement(searchBoxLocator);
            searchBoxWebelement.sendKeys(brokerName);
            searchBoxWebelement.sendKeys(Keys.ENTER);
            logger.debug("The data '{}' has been entered into search, and the Enter key has been clicked", brokerName);

            brokerPageValidation.validateAttribute(brokerPage.getBrokerSearchBox(), "value", brokerName);
            ValidationUtils.isElementDisplayed(brokerPage.getFirstBrokerText());
            brokerPageValidation.validateText(brokerPage.getFirstBrokerText(), brokerName);
            logger.info("Validation of broker '{}' in the search box is successful.", brokerName);
        } catch (Exception e) {
            logger.error("Error occurred while validating the broker '{}' by search.", brokerName, e);
        }
    }

    /**
     * Validates whether the deleted broker is no longer visible.
     */
    public void validateTheDeletedBroker() {
        try {
            ValidationUtils.waitForElementToBeInvisible(brokerPage.getFirstBrokerText(), 60);
            logger.info("The deleted broker is no longer visible.");
        } catch (Exception e) {
            logger.error("Error occurred while validating the deleted broker.", e);
        }
    }

    /**
     * Deletes a broker.
     */
    private void deleteTheBroker(String brokerName) {
        try {
            ValidationUtils.waitForVisibility(brokerPage.getDeleteFirstBrokerName());
            ((JavascriptExecutor) Baseclass.getDriver()).executeScript("arguments[0].click();", brokerPage.getDeleteFirstBrokerName());
            ValidationUtils.waitForVisibility(brokerPage.getDeleteWarningDailogBox());
            ValidationUtils.waitForVisibility(brokerPage.getNoButtonOfDeleteBroker());
            ValidationUtils.waitForVisibility(brokerPage.getYesButtonOfDeleteBroker());
            brokerPageValidation.validateText(brokerPage.getAreYouSureToDeleteText(),
                    "Are you sure you want to delete broker " + brokerName + "?");
            ((JavascriptExecutor) Baseclass.getDriver()).executeScript("arguments[0].click();", brokerPage.getYesButtonOfDeleteBroker());
            logger.info("The Yes button has been clicked to delete broker '{}'.", brokerName);
        } catch (Exception e) {
            logger.error("Error occurred while deleting broker '{}'.", brokerName, e);
        }
    }

    /**
     * Handles pagination and collects all item data from all pages into a single List<String>.
     */
    public List<String> getAllColumnDataWithPagination(WebElement nextButtonElement, By columnLocator) {
        List<String> allColumnData = new ArrayList<>();
        WebDriverWait wait = new WebDriverWait(Baseclass.getDriver(), Duration.ofSeconds(10));

        try {
            while (true) {
                // Wait for column elements on the current page
                List<WebElement> columnElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(columnLocator));

                for (WebElement cell : columnElements) {
                    try {
                        allColumnData.add(cell.getText().trim());
                    } catch (StaleElementReferenceException e) {
                        logger.warn("Stale element encountered for column data, skipping.");
                    }
                }

                // Check whether the "Next" button is enabled
                String nextBtnState = nextButtonElement.getAttribute("class");
                if (!nextButtonElement.isEnabled() || nextBtnState.contains("disabled")) {
                    logger.info("Reached the last page; 'Next' button is disabled.");
                    break;
                }

                // Click the "Next" button and wait for page refresh
                try {
                    nextButtonElement.click();
                    wait.until(ExpectedConditions.stalenessOf(columnElements.get(0)));
                } catch (ElementNotInteractableException | StaleElementReferenceException e) {
                    logger.warn("Error interacting with 'Next' button. Re-fetching it.");
                }

                // Re-fetch the Next button after the page refresh
                try {
                    nextButtonElement = wait.until(ExpectedConditions.elementToBeClickable(nextButtonElement));
                } catch (NoSuchElementException | TimeoutException e) {
                    logger.error("Unable to locate 'Next' button, stopping pagination.", e);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error occurred during pagination.", e);
        }

        logger.info("Pagination completed. Total items collected: {}", allColumnData.size());
        return allColumnData;
    }
}