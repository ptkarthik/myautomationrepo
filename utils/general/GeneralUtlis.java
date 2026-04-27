package org.billing.utils.general;

import org.billing.api.responses.get.GetAllOrders;
import org.billing.core.Baseclass;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneralUtlis {
    /**
     * Helper method to scroll to an element using JavaScript.
     *
     * @param driver  Selenium WebDriver instance.
     * @param element The WebElement to scroll to.
     */
    public static void scrollToElement(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });", element);
            System.out.println("[INFO] Scrolled to element: " + element);
        } catch (Exception e) {
            throw new RuntimeException("Failed to scroll to the element: " + element, e);
        }
    }

    /**
     * Utility method to click on a WebElement using JavaScriptExecutor.
     *
     * @param element The WebElement to click.
     */
    public static void clickUsingJS(WebElement element) {
        if (element == null) {
            throw new NullPointerException("WebElement is null. Cannot perform JS click operation.");
        }
        try {
            // Fetch WebDriver from the WebElement and cast it to JavascriptExecutor
            JavascriptExecutor jsExecutor = (JavascriptExecutor) Baseclass.getDriver();

            // Perform the JavaScript click
            jsExecutor.executeScript("arguments[0].click();", element);

            System.out.println("[INFO] Clicked the WebElement using JavaScriptExecutor.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to click the WebElement using JavaScriptExecutor.", e);
        }
    }

    public static Map<String, List<GetAllOrders>> groupOrders(List<GetAllOrders> orders, Function<GetAllOrders, String> keyExtractor) {
        return orders.stream().collect(Collectors.groupingBy(keyExtractor));
    }


    public static String getFirstDateOfCurrentMonthIsoUtc() {
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        ZonedDateTime zdt = firstDay.atStartOfDay(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return zdt.format(formatter);
    }

    public static String getFirstDateOfGivenMonthIsoUtc(String yearMonth) {
        // yearMonth should be in "yyyy-MM" format
        LocalDate firstDay = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ZonedDateTime zdt = firstDay.atStartOfDay(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return zdt.format(formatter);
    }
}
