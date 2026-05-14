package org.billing.services.ui;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.api.endpoints.EndPoints;
import org.billing.api.payloads.authentication.AuthBody;
import org.billing.utils.propconfig.PropertiesReader;

public class BaseServices {
    private static final Logger logger = LogManager.getLogger(BaseServices.class);
    @Override
    public String toString() {
        return super.toString();
    }
}
