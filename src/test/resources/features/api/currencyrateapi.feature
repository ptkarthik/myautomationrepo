# Feature File: Optimized Currency Rate API Testing with Data Tables
# This file uses Data Tables to streamline testing of the Currency Rate API.

Feature: Currency Rate API Functional and Security Tests
   This feature tests the core functionality and security of the Currency Rate API with reusable data tables.
    @Regression
  Scenario Outline: Validate GET Requests for Existing and Empty Data
    # Tests the GET endpoint for both valid and empty database scenarios.
    When a GET request is sent to "<endpoint>" with "<authorization_status>" authorization
    Then the response status should be <expected_response>
    And validate if the response is matching with DB
    Examples:
      | endpoint                                  | authorization_status | expected_response |
      | /api/currency-rates | valid                | 200               |

      @Regression
  Scenario Outline: Validate POST Requests with Valid and Missing Payloads
    # Covers data creation and validation errors for POST requests.
    Given the following payload for the POST request:
      | monthYear       | code    | rate |
     | <monthYear>     | <code>  | <rate> |
    When a POST request is sent to "<endpoint>" with "<authorization_status>" authorization
    Then the response status should be <expected_response>
    And check the response is displayed in response List

    Examples:
      | monthYear       | code    | rate | endpoint                | authorization_status | expected_response |
      | October-2025      | INReee     | 20.2232   | /api/currency-rates     | valid                | 201           |
