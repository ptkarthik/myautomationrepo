package org.billing.api.APIUtils;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.api.endpoints.EndPoints;
import org.billing.api.payloads.authentication.AuthBody;
import org.billing.utils.propconfig.PropertiesReader;

public class APIAuthentication {
    private static final Logger logger = LogManager.getLogger(APIAuthentication.class);

    public static String postAuthorizationCode() {

        // Define username and password credentials (as a JSON payload)
        Headers headers = new Headers(new Header("Content-Type", "application/json"));
        AuthBody authBody = new AuthBody(PropertiesReader.getProperty("username"),
                PropertiesReader.getProperty("password"));
        String response = RestAssured.given().
                baseUri(PropertiesReader.getProperty("baseUrl")).headers(headers).body(authBody).when().
                post(EndPoints.BROKER_AUTH).then().extract().response().asString();
        logger.info(response);
        logger.debug(response);
        JsonPath jsonPath = new JsonPath(response);
        return jsonPath.getString("id_token");
    }
}
