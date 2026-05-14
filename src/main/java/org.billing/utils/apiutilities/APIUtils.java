package org.billing.utils.apiutilities;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class APIUtils {

    public static Response get(String endpoint, Map<String, String> queryParams) {
        return given()
                .queryParams(queryParams)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }

    public static Response getWithAuth(String baseURI, String endpoint,
                                       Map<String, String> queryParams, List<Header> headers) {
        return given().baseUri(baseURI)
                .queryParams(queryParams)
                .headers(new Headers(headers))
                .contentType(ContentType.JSON).log().all()
                .when()
                .get(endpoint)
                .then().log().all()
                .extract()
                .response();
    }

    public static Response getWithQueryParams(String baseURI, String endpoint,
                                              Map<String, String> queryParams, List<Header> headers) {
        return given().baseUri(baseURI)
                .queryParams(queryParams)
                .headers(new Headers(headers)).log().all()
                .when()
                .get(endpoint)
                .then().log().all()
                .extract()
                .response();
    }

    public static Response getWithPathParams(String baseURI, String endpoint,
                                             Map<String, String> pathParams, List<Header> headers) {
        return given().baseUri(baseURI)
                .pathParams(pathParams)
                .headers(new Headers(headers)).log().all()
                .when()
                .get(endpoint)
                .then().log().all()
                .extract()
                .response();
    }


    public static Response getAllWithAuth(String baseURI, String endpoint, List<Header> headers) {
        return given().baseUri(baseURI)
                .headers(new Headers(headers))
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }

    public static Response post(String endpoint, String body) {
        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public static Response postWithAuth(String baseURI, String endpoint, Object body, List<Header> headers) {
        return given()
                .baseUri(baseURI)
                .contentType(ContentType.JSON)
                .headers(new Headers(headers))
                .log().all()
                .body(body)
                .when()
                .post(endpoint)
                .then().log().all()
                .extract()
                .response();
    }
    public static Response postWithAuth(String baseURI, String endpoint, String body, List<Header> headers) {
        return given()
                .baseUri(baseURI)
                .contentType(ContentType.JSON)
                .headers(new Headers(headers)).log().all()
                .body(body)
                .when()
                .post(endpoint)
                .then().log().all()
                .extract()
                .response();
    }

    public static Response uploadFile(String baseURI, String endpoint, Map<String, Object> queryParams,
                                      String fileParamName, File fileToUpload, List<Header> headers) {
        // Validation: Check if the file exists
        if (!fileToUpload.exists()) {
            throw new RuntimeException("File does not exist at path: " + fileToUpload.getAbsolutePath());
        }

        return RestAssured.given()
                .baseUri(baseURI)                         // Set base URI
                .queryParams(queryParams)                 // Add query parameters
                .headers(new Headers(headers))            // Add headers
                .multiPart(fileParamName, fileToUpload, "text/csv")   // Attach the file as form data
                .contentType("multipart/form-data")       // Content type for file upload
                .when()
                .post(endpoint)                           // Perform POST request
                .then()
                .extract()
                .response();                              // Extract the response
    }

    public static Response uploadFileWithPathParam(String baseURI, String endpoint, Map<String, Object> pathParams,
                                      String fileParamName, File fileToUpload, List<Header> headers) {
        // Validation: Check if the file exists
        if (!fileToUpload.exists()) {
            throw new RuntimeException("File does not exist at path: " + fileToUpload.getAbsolutePath());
        }

        return RestAssured.given()
                .baseUri(baseURI)                         // Set base URI
                .pathParams(pathParams)                // Add query parameters
                .headers(new Headers(headers))
                .log().all()// Add headers
                .multiPart(fileParamName, fileToUpload,"text/csv")   // Attach the file as form data
                .contentType("multipart/form-data")       // Content type for file upload
                .when()
                .post(endpoint)                           // Perform POST request
                .then()
                .log().all()
                .extract()
                .response();                              // Extract the response
    }

    public static Response putWithAuth(String baseURI, String endpoint, String body, List<Header> headers) {
        return given()
                .baseUri(baseURI)
                .contentType(ContentType.JSON)
                .headers(new Headers(headers))
                .body(body)
                .when()
                .put(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static Response putWithAuthAndPath(String baseURI, Map<String, String> pathData, String endpoint, String body, List<Header> headers) {
        return given()
                .baseUri(baseURI)
                .pathParams(pathData)
                .headers(new Headers(headers))
                .body(body)
                .when()
                .put(endpoint)
                .then()
                .extract()
                .response();
    }

    public static Response putWithAuthAndPath(String baseURI, Map<String, String> pathData,String endpoint, List<Header> headers) {
        return given()
                .baseUri(baseURI)
                .pathParams(pathData)
                .log().all()
                .headers(new Headers(headers))
                .when()
                .put(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static Response deleteWithAuth(String baseURI, Map<String, String> pathData,
                                          String endpoint, List<Header> headers) {
        return given()
                .baseUri(baseURI)
                .pathParams(pathData)
                .headers(new Headers(headers))
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();
    }

    public static Response deleteWithAuth(String baseURI, String endpoint, List<Header> headers) {
        return given()
                .baseUri(baseURI)
                .headers(new Headers(headers))
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();
    }

    public static Response deleteWithAuthAndPath(String baseURI, Map<String, String> pathData, String endpoint, List<Header> headers) {
        return given()
                .baseUri(baseURI)
                .pathParams(pathData)
                .headers(new Headers(headers))
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();
    }
}