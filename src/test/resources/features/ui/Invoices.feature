Feature: Login and Navigate to Invoices

  Scenario: Generate invoice for a Broker with VOLUME BASED billing method
    Given The user is in the login page of Billing application
    When The user enters valid credentials as "web.automation@tora.com", "Default1"
    And The user navigates to the home page
    And The user selects the month-year "December-2024"
    And The user filters the records by broker "ADAM"
    Then The broker "ADAM" is displayed in the search result
    And The user verifies the number of versions available for "ADAM"
    When The user generates a new build for "ADAM"
    Then A new version should be generated for "ADAM"


  Scenario: Generate invoice for a Broker with FIXED RATE billing method
    Given The user is in the login page of Billing application
    When The user enters valid credentials as "web.automation@tora.com", "Default1"
    And The user navigates to the home page
    And The user selects the month-year "November-2024"
    And The user selects the billing method to "FIXED RATE"
    And The user filters the records by broker "baml"
    Then The broker "baml" is displayed in the search result
    And The user verifies the number of versions available for "baml"
    When The user generates a new fast invoice build for "baml"
    Then A new version should be generated for "baml"


  Scenario: Verify the Cap Rule for a Broker/Client
    Given The user is in the login page of Billing application
    When The user enters valid credentials as "web.automation@tora.com", "Default1"
    And The user navigates to the "Cap Rules" page
    And The user fetch the value of client as "KADENSA" and broker as "jpm"
    And The user navigates to the "View Orders" page
    And The user selects the month with client as "KADENSA" and broker as "jpm"
    Then The user verifies the value and the tora commission


  Scenario: Verify the Rate Rule for a Broker/Client
    Given The user is in the login page of Billing application
    When The user enters valid credentials as "web.automation@tora.com", "Default1"
    And The user navigates to the "Rate Rules" page
    And The user fetch the value of client as "KADENSA", broker as "baml" and market as "FUT.IDX.NIFTY" in rate rule page
    And The user navigates to the "View Orders" page
    And The user selects the month with client as "KADENSA", broker as "baml" and market as "FUT.IDX.NIFTY"
    Then The user verifies the value and the tora rate



