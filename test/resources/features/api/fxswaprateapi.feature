Feature: FxSwapRates API Automation
  As a QA engineer
  I want to validate CRUD operations for the FxSwapRates API endpoints dynamically
  So that I can test positive, negative, and edge cases for the system


  # Scenario: Validate POST (Create FxSwapRate)
  @Regression
    @ignore
  Scenario: Validate creating a new FxSwapRate entry
    Given I create a valid POST request payload for FxSwapRate data
      | tenor   | upperBoundInDays | lowerBoundInDays | revenueShare | maxUpperBound |
      | TestOne | 1760             | 1757             | 0.025440008  | false         |
    When I send a POST request to the endpoint
    Then The API should return a 201 Created response
    And The response body should include a unique FxSwapRate ID
    And The response payload should match the request payload

#   Scenario: Validate PUT (Update FxSwapRate)
  @Regression
    @ignore
  Scenario: Validate updating an existing FxSwapRate entry
    Given I create a new FxSwapRate entry via POST request:
      | tenor   | upperBoundInDays | lowerBoundInDays | revenueShare | maxUpperBound |
      | TestTwo | 6080             | 6077             | 0.0350       | false         |
    And I store the ID of the newly created FxSwapRate
    When I create a PUT request payload to update the FxSwapRate properties:
      | tenor     | upperBoundInDays | lowerBoundInDays | revenueShare |
      | Testthree | 6080             | 6078             | 0.05100      |
    And I send a PUT request to the endpoint with the stored ID
    Then The API should return a 200 OK response
    And The response body should reflect the updated FxSwapRate properties

    @ignore
  # Scenario: Validate DELETE (Invalid ID)
  Scenario: Validate deleting an FxSwapRate with an invalid/nonexistent ID
    Given I generate a random invalid FxSwapRate ID (e.g., 999)
    When I send a DELETE request to the endpoint with the invalid ID
    Then The API should return a 404 Not Found response


  @Regression
    @ignore
  # Scenario: Validate DELETE (Valid ID)
  Scenario: Validate deleting an FxSwapRate with a valid ID
    Given I create a new FxSwapRate entry via POST request:
      | tenor       | upperBoundInDays | lowerBoundInDays | revenueShare | maxUpperBound |
      | "ValidTest" | 6110             | 6101             | 0.0350       | false         |
    And I store the ID of the newly created FxSwapRate
    When I send a DELETE request to the endpoint with the stored ID
    Then The API should return a 204 No Content response
    And The entry should no longer exist when checked with a GET request

  # Scenario: Validate POST (Boundary Value Analysis)
  Scenario: Validate field constraints for FxSwapRate creation
    Given I create a POST request payload with field exceeding limits:
      | tenor           | upperBoundInDays  | lowerBoundInDays | revenueShare | maxUpperBound |
      | "ValidTestFour" | 17606576757566765 | 50000            | 0.025440008  | false         |
    When I send a POST request to the endpoint using jsonpath
    Then The API should return a 400 Bad Request response