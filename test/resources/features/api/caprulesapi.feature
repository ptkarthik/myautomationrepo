Feature: E2E Testing for CapRules API

  @Regression
  Scenario: Create and Delete a CapRule
    Given I send a POST request "/api/cap-rules/create" to the CapRule endpoint with the following body:
      | priority | capType | clientName | brokerName | traderName | securityType | excludeCountry | countriesList           | orderType | side   | instrument | startDate                | endDate                  | value | currency |
      | 01       | DAILY   | ABNEL    | ADAM     | Test     | ANY          | true           | ["CANADA", "UK", "USA"] | ALGO    | ANY | ANY     | 2025-07-06T14:05:04.405Z | 2025-07-06T14:05:04.405Z | 100     | USD      |
    Then I should receive a response with status code 200
    And The response should confirm the CapRule has been successfully processed
    When I send a delete request to the endpoint "api/cap-rules/{id}"
    Then I should receive a response with status code 204

    @Regression
  Scenario: Create and increment/decrement priority
    Given I send a POST request "/api/cap-rules/create" to the CapRule endpoint with the following body:
      | priority | capType | clientName | brokerName | traderName | securityType | excludeCountry | countriesList           | orderType | side   | instrument | startDate                | endDate                  | value | currency |
      | 01       | DAILY   | 22NW    | ADAM     | Test     | ANY          | true           | ["CANADA", "UK", "USA"] | DMA    | ANY | ANY     | 2025-07-06T14:05:04.405Z | 2025-07-06T14:05:04.405Z | 150     | USD      |
    Then I should receive a response with status code 200
    And The response should confirm the CapRule has been successfully processed
    When I send a POST request to fetch the CapRule with endpoint "api/cap-rules"
    When I increase the priority of the CapRule with endpoint "api/cap-rules/increase-priority/{id}"
    Then I should receive a response with status code 200
    When I send a POST request to fetch the CapRule with endpoint "api/cap-rules"
   # And The response should confirm the priority of the CapRule is "increased"
    When I decrease the priority of the CapRule with endpoint "api/cap-rules/decrease-priority/{id}"
    Then I should receive a response with status code 200
    When I send a POST request to fetch the CapRule with endpoint "api/cap-rules"
   # And The response should confirm the priority of the CapRule is "decreased"
    When I send a delete request to the endpoint "api/cap-rules/{id}"
    Then I should receive a response with status code 204

      @Regression
  Scenario: Create and Modify the Cap-Rule
    Given I send a POST request "/api/cap-rules/create" to the CapRule endpoint with the following body:
      | priority | capType | clientName | brokerName | traderName | securityType | excludeCountry | countriesList           | orderType | side   | instrument | startDate                | endDate                  | value | currency |
      | 100      | DAILY   | 22NW   | AES | Test     | ANY          | true           | ["CANADA", "UK", "USA"] | DMA    | ANY | ANY     | 2025-07-06T14:05:04.405Z | 2025-07-06T14:05:04.405Z | 10     | USD      |
    Then I should receive a response with status code 200
    And The response should confirm the CapRule has been successfully processed
    When I send a PUT request to the endpoint "api/cap-rules" by updating the clientName to "AASPC"
    Then I should receive a response with status code 200
    And The response should confirm the CapRule has been successfully processed
    When I send a delete request to the endpoint "api/cap-rules/{id}"
    Then I should receive a response with status code 204

    Scenario: Create Cap-Rules by bulk upload without headers
      When I send a POST request to the endpoint "api/cap-rules/upload-csv/{isHeaderPresent}" with path parameter "isHeaderPresent" set to "false" and I attach the file "caprules.csv" as form data
       Then I should receive a response with status code 200 for upload

  Scenario: Create Cap-Rules by bulk upload with headers
    When I send a POST request to the endpoint "api/cap-rules/upload-csv/{isHeaderPresent}" with path parameter "isHeaderPresent" set to "true" and I attach the file "caprules-headers.csv" as form data
    Then I should receive a response with status code 200 for upload