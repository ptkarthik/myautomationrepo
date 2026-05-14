Feature: E2E Testing for TemplateRules API

  Scenario: Create and Delete a TemplateRule for VOLUME - BROKER
    Given I send a GET request "/api/template-rules/BROKER/VOLUME/get-max-priority" to get max priority endpoint
    Given I send a POST request "api/template-rules/BROKER/VOLUME/create" to the TemplateRule endpoint with the following body:
      | billingMethod | excludedAssetClassesString | market | orderType | region | sourceEnvironment | synthType           | templateName |traderType|
      | VOLUME      |  FUT                   | CS.NULL.JAPAN     | SWAP     | APAC          | TTS           | SWAP| BERN                  |SWAP      |
    Then I should receive a response with status code 200
    And The response should confirm the TemplateRule has been successfully processed
    When I send a delete request to the endpoint "api/template-rules/BROKER/VOLUME/{id}"
    Then I should receive a response with status code 204

  Scenario: Create and Delete a TemplateRule for VOLUME - CLIENT
    Given I send a GET request "/api/template-rules/CLIENT/VOLUME/get-max-priority" to get max priority endpoint
    Given I send a POST request "api/template-rules/CLIENT/VOLUME/create" to the TemplateRule endpoint with the following body:
      | billingMethod| excludedAssetClassesString | market | orderType | region | sourceEnvironment | synthType           | templateName |traderType|
      | VOLUME        | FUT                             | CS.NULL.JAPAN     | SWAP     | APAC          | TTS           | SWAP| ARCTOS                 |SWAP      |
    Then I should receive a response with status code 200
    And The response should confirm the TemplateRule has been successfully processed
    When I send a delete request to the endpoint "api/template-rules/CLIENT/VOLUME/{id}"
    Then I should receive a response with status code 204
#
  Scenario: Create and Delete a TemplateRule for FIXED
    Given I send a GET request "/api/template-rules/BROKER/FIXED/get-max-priority" to get max priority endpoint
    Given I send a POST request "api/template-rules/BROKER/FIXED/create" to the TemplateRule endpoint with the following body:
      | billingMethod | region | sourceEnvironment | synthType           | templateName |
      | FIXED       | APAC          | TTS           | SWAP| EAXIS    |
    Then I should receive a response with status code 200
    And The response should confirm the TemplateRule has been successfully processed
    When I send a delete request to the endpoint "api/template-rules/BROKER/FIXED/{id}"
    Then I should receive a response with status code 204

  Scenario: Create and Delete a TemplateRule for FIXED - CLIENT
    Given I send a GET request "/api/template-rules/CLIENT/FIXED/get-max-priority" to get max priority endpoint
    Given I send a POST request "api/template-rules/CLIENT/FIXED/create" to the TemplateRule endpoint with the following body:
      | billingMethod  | region | sourceEnvironment | synthType           | templateName |
      | FIXED        | APAC          | TTS           | SWAP| ASWAVE    |
    Then I should receive a response with status code 200
    And The response should confirm the TemplateRule has been successfully processed
    When I send a delete request to the endpoint "api/template-rules/CLIENT/FIXED/{id}"
    Then I should receive a response with status code 204

  Scenario: Create and Delete a TemplateRule for CONNECTIVITY - BROKER
    Given I send a GET request "/api/template-rules/BROKER/CONNECTIVITY/get-max-priority" to get max priority endpoint
    Given I send a POST request "api/template-rules/BROKER/CONNECTIVITY/create" to the TemplateRule endpoint with the following body:
      | billingMethod |  templateName |
      | CONNECTIVITY       |  CBNP |
    Then I should receive a response with status code 200
    And The response should confirm the TemplateRule has been successfully processed
    When I send a delete request to the endpoint "api/template-rules/BROKER/CONNECTIVITY/{id}"
    Then I should receive a response with status code 204

  Scenario: Create and Delete a TemplateRule for CONNECTIVITY - CLIENT
    Given I send a GET request "/api/template-rules/CLIENT/CONNECTIVITY/get-max-priority" to get max priority endpoint
    Given I send a POST request "api/template-rules/CLIENT/CONNECTIVITY/create" to the TemplateRule endpoint with the following body:
      | billingMethod |   templateName |
      | CONNECTIVITY       |  CHORIZON    |
    Then I should receive a response with status code 200
    And The response should confirm the TemplateRule has been successfully processed
    When I send a delete request to the endpoint "api/template-rules/CLIENT/CONNECTIVITY/{id}"
    Then I should receive a response with status code 204

  Scenario: Create and Delete a TemplateRule for AGENCY - BROKER
    Given I send a GET request "/api/template-rules/BROKER/AGENCY/get-max-priority" to get max priority endpoint
    Given I send a POST request "api/template-rules/BROKER/AGENCY/create" to the TemplateRule endpoint with the following body:
      | billingMethod |     templateName |region|sourceEnvironment|
      | AGENCY      |ABGC    |APAC|EVO          |
    Then I should receive a response with status code 200
    And The response should confirm the TemplateRule has been successfully processed
    When I send a delete request to the endpoint "api/template-rules/BROKER/AGENCY/{id}"
    Then I should receive a response with status code 204

  Scenario: Create and Delete a TemplateRule for DEVELOPMENT - BROKER
    Given I send a GET request "/api/template-rules/BROKER/CONSULTING/get-max-priority" to get max priority endpoint
    Given I send a POST request "api/template-rules/BROKER/CONSULTING/create" to the TemplateRule endpoint with the following body:
      | billingMethod |    templateName |
      | CONSULTING       |  DBNP    |
    Then I should receive a response with status code 200
    And The response should confirm the TemplateRule has been successfully processed
    When I send a delete request to the endpoint "api/template-rules/BROKER/CONNECTIVITY/{id}"
    Then I should receive a response with status code 204
