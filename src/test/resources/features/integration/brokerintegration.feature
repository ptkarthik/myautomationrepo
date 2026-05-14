Feature: validate the Broker Tab Page

  Scenario: Navigate to Broker Page and Add a New Broker
    Given The user is in the login page of Billing application
    When The user enters valid credentials as "user", "user"
    And The user navigates to the broker page
    And The user Click to Add New broker
    When the user enters the following broker details
      | BrokerName |
      | TestBroker|
    And the user saves the broker
    Given I fetch given broker details with Name
    And I validate the broker name in API response
    And I validate the broker in DataBase
