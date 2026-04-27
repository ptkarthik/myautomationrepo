package org.billing.utils.billing;

import org.billing.core.Baseclass;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableUtil extends Baseclass {


    public static List<WebElement> getRowsStream(WebElement table, String tbody, String tr) {
        return table.findElement(By.tagName(tbody)).findElements(By.tagName(tr));
    }

    public List<String> fetchTableDataUsingColumnName(String columnName) {
        final List<String> tableDataColumn = new ArrayList<>();
        WebElement webTable = getDriver().findElement(By.cssSelector("div.ui-datatable > div:nth-child(2) > table"));
        List<String> webTableHeaders = webTable.findElements(By.cssSelector("thead > tr > th")).stream()
                .map(e -> e.findElement(By.className(
                        "ui-column-title"))).map(WebElement::getText).collect(Collectors.toList());
        final int columnIndex = webTableHeaders.indexOf(columnName);

        if (columnIndex < 0) {
            throw new IllegalArgumentException("Column " + columnName + " does not exist");
        }

        webTable.findElements(By.cssSelector("tbody > tr")).forEach(e -> {
            tableDataColumn.add(e.findElements(By.tagName("td")).get(columnIndex).getText());
        });
        return tableDataColumn;
    }

    public List<String> extractTableDataColumn(WebElement table, String column) {
        waitABit(2000);
        final List<String> tableDataColumn = new ArrayList<>();
        final List<String> thElements = getHeader(table);
        final int columnIndex = thElements.indexOf(column);

        if (columnIndex < 0) {
            throw new IllegalArgumentException("Column " + column + " does not exist");
        }

        getRowsStream(table, "tbody", "tr").forEach(e -> {
            for (int retry = 0; retry < 3; retry++) {
                try {
                    tableDataColumn.add(e.findElements(By.tagName("td")).get(columnIndex).getText());
                    break;
                } catch (StaleElementReferenceException ex) {
                    if (retry == 2) {
                        throw ex;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(ie);
                    }
                }
            }
        });
        return tableDataColumn;
    }

    public List<String> extractTableDataColumnForHideInvalid(WebElement table, String columnName) {

        final List<String> tableDataColumn = new ArrayList<>();
        WebElement webTable = getDriver().findElement(By.cssSelector("div.ui-datatable > div:nth-child(2) > table"));
        List<String> webTableHeaders = webTable.findElements(By.cssSelector("thead > tr > th")).stream()
                .map(e -> e.findElement(By.className(
                        "ui-column-title"))).map(WebElement::getText).collect(Collectors.toList());
        final int columnIndex = webTableHeaders.indexOf(columnName);

        if (columnIndex < 0) {
            throw new IllegalArgumentException("Column " + columnName + " does not exist");
        }
        webTable.findElements(By.cssSelector("tbody > tr")).forEach(e -> {
            List<WebElement> cells = e.findElements(By.tagName("td"));
            tableDataColumn.add(cells.get(0).findElement(By.tagName("img")).getDomAttribute("src"));

        });
        return tableDataColumn;
    }

    private static List<String> getHeader(WebElement table) {
        return getRowsStream(table.findElement(By.tagName("thead")), "tr", "th").stream()
                .map(e -> e.findElement(By.className(
                        "ui-column-title")))
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public static WebElement getColumnHeaderElement(WebElement table, String columnName) {
        return table
                .findElement(By.tagName("thead"))
                .findElement(By.tagName("tr"))
                .findElements(By.tagName("th"))
                .stream()
                .filter(th -> columnName.equals(getColumnTitle(th)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such column: " + columnName));
    }

    private static String getColumnTitle(WebElement th) {
        WebElement titleElement = th.findElement(By.className("ui-column-title"));
        return titleElement.getText();
    }
}
