@ignore
Feature: End-to-End Testing for Traders API

  @Regression
  Scenario: Retrieve all traders without pagination
    Given I send a GET request to the endpoint
    Then I should receive a response with status code 200 for GetAll
    And the response should include a list of traders

  Scenario: Retrieve a paginated list of traders
    When I send a GET request to the endpoint with the following query parameters:
      | size | page  |
      | 5    | 18000 |
    Then I should receive a response with status code 200
    And The response should include an empty array

  @Regression
  Scenario: Upload a valid CSV file to add traders
    When I send a POST request to the endpoint with the following query parameters and I attach the file as form data:
      | isHeaderPresent | filePath                |
      | false           | trader_CSV_NoHeader.csv |
    Then I should receive a response with status code 200 for upload
    And the response should match the file uploaded vs the CSV files

  @Regression
  Scenario: Create a new trader with valid name
    # Positive case: Valid name
    Given I send a POST request to the endpoint with the following body:
      | id   | name                              | isToraTrader |
      | 33sd | Valid Trddfgddsdfsdfsdsfdfsddader | true         |
    Then I should receive a response with status code 201
    And The response should confirm the trader has been successfully created

  Scenario: Update a trader's details with invalid name data
      # Prerequisite: Create a trader for update testing
    Given I send a POST request to the endpoint with the following body:
      | id | name          | isToraTrader |
      | DF | Original Name | true         |
    Then I should receive a response with status code 201
    And The response should confirm the trader has been successfully created
#
    # Negative case: Name is empty during update
    When I send a PUT request to the endpoint "api/traders/<TraderId>" with the following body:
      | id         | name | isToraTrader |
      | <TraderId> |      | true         |
    Then I should receive a response with status code 400
    And The response should contain an error message indicating "A name must be specified for the trader!"
#
    # Negative case: Name exceeds maximum length during update
    When I send a PUT request to the endpoint "api/traders/<TraderId>" with the following body:
      | id         | name                                                  | isToraTrader |
      | <TraderId> | A name that exceeds maximum allowed length. Too long. | true         |
    Then I should receive a response with status code 400
    And The response should contain an error message indicating "Trader name must be less than 50 characters!"

  @Regression
  Scenario: Delete an existing trader
    # Prerequisite: Create a trader for deletion
    Given I send a POST request to the endpoint with the following body:
      | id     | name          | isToraTrader |
      | 4dssdf | Delete Trader | true         |
    Then I should receive a response with status code 201
    And The response should confirm the trader has been successfully created

    # Delete the trader
    When I send a DELETE request to the endpoint "api/traders/4dssdf"
    Then I should receive a response with status code 204
    And The response should confirm the trader has been successfully deleted