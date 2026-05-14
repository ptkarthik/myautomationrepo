package org.billing.pages;

import org.billing.core.Baseclass;
import org.openqa.selenium.support.PageFactory;

public class BasePage {
    public BasePage() {

        PageFactory.initElements(Baseclass.getDriver(), this);
    }
}
