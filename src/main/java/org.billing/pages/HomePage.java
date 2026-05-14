package org.billing.pages;

import org.billing.core.Baseclass;
import org.openqa.selenium.By;

public class HomePage extends Baseclass {

    private static final By MONTH_YEAR_LINK = By.linkText("December-2024");
    private static final By HOME_LINK = By.linkText("Home");


    public void navigateToHomePage() {
        clickElement(HOME_LINK,10);
    }

    public void selectMonthYear(String monthYear) {
        waitUntilElementIsVisible(MONTH_YEAR_LINK, 10);
       clickElement(MONTH_YEAR_LINK,10);
    }
}
