package org.billing.pages;

import org.billing.core.Baseclass;
import org.openqa.selenium.By;

public class LoginPage extends Baseclass {
    private static final By     textbox_username = By.id("username");
    private static final By textbox_password = By.id("password");
    private static final By btn_login = By.xpath("//button[contains(text(),'Login')]");

    public void enterEmail(String email) {
        waitUntilElementIsVisible(textbox_username,10);
        getDriver().findElement(textbox_username).sendKeys(email);
    }

    public void enterPassword(String password) {
        waitUntilElementIsVisible(textbox_password,10);
        getDriver().findElement(textbox_password).sendKeys(password);
    }

    public void clickLogIn() {
        clickElement(btn_login,10);
    }
}
