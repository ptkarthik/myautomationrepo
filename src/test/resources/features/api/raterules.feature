Feature: CRUD operations for Rate Rules with multiple billing methods

  As a tester
  I want to create and update rate rules for different billing methods
  So that I can validate the API for each method generically

  @Regression
  Scenario Outline: Create a rate rule for a billing method
    Given the billing method is "<BillingMethod>"
    And I have a valid request payload with <client>, <broker> and <rate>
      | client | broker | rate |
      | SMBC   | gs     | 50   |
    Then the response should match the expected "<BillingMethod>" response

    Examples:
      | BillingMethod |
      | CONNECTIVITY        |
      | VOLUME        |
      | FIXED         |
      | DEVELOPMENT   |
      | AGENCY        |

  @Regression
  Scenario Outline: Delete a rate rule for a billing method
    Given the billing method is "<BillingMethod>"
    And I have a valid request payload with <client>, <broker> and <rate>
      | client | broker | rate |
      | SMBC   | gs     | 50   |
    When I send a DELETE request to the rate rule endpoint
    Then the response of deletion of rate rule status should be 204

    Examples:
      | BillingMethod |
      | VOLUME        |
      | CONNECTIVITY  |
      | AGENCY        |
      | DEVELOPMENT   |
#
  @Regression
  Scenario Outline: Get all rate rule for a billing method
    Given the following rate rule to fetch:
      | billingMethod   |
      | <BillingMethod> |
    When I send a GET request to the rate rule endpoint
    Then the response status should be 200

    Examples:
      | BillingMethod |
      | fixed         |
      | Volume        |
      | Connectivity  |
