Feature: Validate Broker API

  @Regression
  Scenario: Verify add/Delete Broker
    Given I add the broker name
      | name    |
      | TestBroker |
    Then I validate the response status code is 201
    And I validate the added broker name
    When I fetch broker details with Name
    Then I validate the response status code is 200
    When i delete the broker
   Then I validate the response status code is 204