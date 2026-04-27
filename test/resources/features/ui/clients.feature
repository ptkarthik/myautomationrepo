Feature: validate the Broker Tab Page

  Scenario: Navigate to clients Page and Add a New Broker
    Given The user is in the login page of Billing application
    When The user enters valid credentials as "web.automation@tora.com", "Default1"
    And The user navigates to the broker page
    And The user Click to Add New broker
    Then User validates the AddBrokerTab Details
    When the user enters the following broker details
      | BrokerName |
      | Zerodha    |
    And the user saves the broker
    Then the broker should be saved successfully
