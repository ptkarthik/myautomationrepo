package org.billing.pages;

import org.billing.utils.billing.TableUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ViewOrdersPage extends TableUtil {


    private static final By brokerInput = By.xpath("//select[contains(@id,'brokerInput')]");
    private static final By clientInput = By.xpath("//select[contains(@id,'clientInput')]");
    private static final By marketInput = By.xpath("//*[contains(@id,'marketInput')]");
    private static final By calendarFrom = By.xpath("//span[text()='From:']/following-sibling::span[1]/img[@class='rich-calendar-button ']");
    private static final By calendarTo = By.xpath("//span[contains(text(),'To:')]/following-sibling::span[1]/img[@class='rich-calendar-button ']");
    private static final By currentCalendarMonth = By.cssSelector("td.rich-calendar-month > .rich-calendar-tool-btn");
    private static final By loader = By.xpath("//img[@src='/broker_invoice/images/load.gif']");
    private static final By search = By.cssSelector("input.button[value='Search']");

    public void selectBroker(String broker) {
        selectByVisibleText(brokerInput, broker);
    }

    public void selectClient(String client) {
        selectByVisibleText(clientInput, client);
    }

    public void enterMarket(String market) {
        getDriver().findElement(marketInput).sendKeys(market);
    }

    public void selectDates() {
        clickElement(calendarFrom, 10);
        String currentMonth = getDriver().findElement(currentCalendarMonth).getText();

        // Date format
        LocalDateTime currentDateTime = LocalDateTime.now().minusMonths(1);
        LocalDate lastDayOfMonth = currentDateTime.toLocalDate().withDayOfMonth(currentDateTime.toLocalDate().lengthOfMonth());
        String previousMonthName = lastDayOfMonth.format(DateTimeFormatter.ofPattern("MMMM"));
        String endDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("d"));

        // From Date
        if (!currentMonth.contains(previousMonthName)) {
            clickElement(By.xpath("(//td[@class='rich-calendar-tool']/div)[2]"), 10);
        }
        clickElement(By.xpath("//td[text()='1' and (contains(@class,'rich-calendar-btn') or contains(@class,'rich-calendar-select'))]"), 10);

        // To Date
        clickElement(calendarTo, 10);
        if (!currentMonth.contains(previousMonthName)) {
            clickElement(By.xpath("//span[contains(text(),'To')]/following-sibling::table//td[@class='rich-calendar-tool'][2]/div"), 10);
        }
        waitABit(1000);
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", getDriver().findElement(By.xpath("//span[contains(text(),'To')]/following-sibling::table//td[text()='" + endDate + "' and contains(@class,'rich-calendar-btn')]")));

    }

    public void search() {
        clickElement(search, 10);
        waitUntilElementIsNotVisible(getDriver().findElement(loader), 100);
    }

    public Integer fetchToraCommission() throws ParseException {
        final List<Integer> tableDataColumn = new ArrayList<>();
        List<WebElement> we = getDriver().findElements(By.xpath("//td[contains(@id,'toraCommissionUSDColumn')]/span"));
        for (int i = 0; i < we.size(); i++) {
            NumberFormat format = NumberFormat.getInstance();
            Number number = format.parse(we.get(i).getText());
            tableDataColumn.add(number.intValue());
        }
        return tableDataColumn.stream().reduce(0, Integer::sum);
    }

    public void fetchToraRate(String expectedToraRate) throws ParseException {
        final List<Integer> tableDataColumn = new ArrayList<>();
        List<WebElement> we = getDriver().findElements(By.xpath("//td[contains(@id,'toraRateColumn')]/span"));
        we.stream().allMatch(e -> {
            String text = e.getText();
            try {
                NumberFormat format = NumberFormat.getInstance();
                Number number = format.parse(text);
                return number.doubleValue() == Integer.parseInt(expectedToraRate) * 0.0001;
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

    }


    public static void main(String[] args) {
        LocalDateTime currentDateTime = LocalDateTime.now().minusMonths(1);
        LocalDate lastDayOfMonth = currentDateTime.toLocalDate().withDayOfMonth(currentDateTime.toLocalDate().lengthOfMonth());
        String previousMonthName = lastDayOfMonth.format(DateTimeFormatter.ofPattern("MMMM"));
        String endDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("d"));
        System.out.println(endDate + " " + previousMonthName);
    }


}
