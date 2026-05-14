Feature: Home Page API Automation

#  @ignore
#  @Regression @E2E
#  Scenario: Apply commission rate rules and verify commissioning for Volume orders
#    Given the system has orders data available via the ViewOrders API
#    When I retrieve orders and group them by broker, client, and other relevant fields
#    And I apply the rate rule using broker, client, and other field details for the current month via the API
#    And I trigger the "EXTRACT_DATA" process for the month via the API
#And I wait for the build to be successfully for current month via the API
#    Then I retrieve the updated orders from the view orders API
#    And I verify that the commission is applied as per the rate rule for each order
#    When I find an "VOLUME" and "BROKER" Invoice template to Link to a template rules
#    And I link the Invoice template created to the template rules via the API


#  @Regression
  Scenario:  Build run for Fixed Rate Broker Orders
    Given I find an "FIXED" and "BROKER" Invoice template to Link to a template rules
    When we select the first "FIXED_RATES" with data and run the build for that month via the API
    Then we get the available brokers and clients details from the orders via the API
    When I send a GET request "/api/template-rules/BROKER/FIXED/get-max-priority" to get max priority endpoint
    And I send a POST request to the create TemplateRule without data table
    And I check the template rules and make the created with highPriority
    Then validate the generated Invoice as per the linked template for the orders via the API
##
#  @Regression
#  Scenario:  Build run for Fixed Rate Client Orders
#    Given I find an "FIXED" and "CLIENT" Invoice template to Link to a template rules
#    When we select the first "FIXED_RATES" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/CLIENT/FIXED/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API
#
#  @Regression
#  Scenario:  Build run for Volume Broker Rate Orders
#    Given I find an "VOLUME" and "BROKER" Invoice template to Link to a template rules
#    When we select the first "EXTRACT_DATA" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/BROKER/VOLUME/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API
##
#  @Regression
#  Scenario:  Build run for Volume Client Orders
#    Given I find an "VOLUME" and "CLIENT" Invoice template to Link to a template rules
#    When we select the first "EXTRACT_DATA" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/CLIENT/VOLUME/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API
#
#  @Regression
#  Scenario:  Build run for Connectivity Broker Rate Orders
#    Given I find an "CONNECTIVITY" and "BROKER" Invoice template to Link to a template rules
#    When we select the first "CONNECTIVITY_RATES" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/BROKER/CONNECTIVITY/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API
#
#    @ignore
#  Scenario:  Build run for Connectivity Client Rate Orders
#    Given I find an "CONNECTIVITY" and "CLIENT" Invoice template to Link to a template rules
#    When we select the first "CONNECTIVITY_RATES" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/CLIENT/CONNECTIVITY/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API
#
#
#  @Regression
#  Scenario:  Build run for Development Broker Rate Orders
#    Given I find an "CONSULTING" and "BROKER" Invoice template to Link to a template rules
#    When we select the first "CONSULTING_RATES" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/BROKER/CONSULTING/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API
##
#  @ignore
#  Scenario:  Build run for Development Client Rate Orders
#    Given I find an "CONSULTING" and "CLIENT" Invoice template to Link to a template rules
#    When we select the first "CONSULTING_RATES" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/CLIENT/CONSULTING/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API
#
#  @ignore
#  Scenario:  Build run for Agency Broker Rate Orders
#    Given I find an "AGENCY" and "BROKER" Invoice template to Link to a template rules
#    When we select the first "NETWORK_RATES" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/BROKER/AGENCY/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API
#
#    @ignore
#  Scenario:  Build run for Agency Client Rate Orders
#    Given I find an "AGENCY" and "CLIENT" Invoice template to Link to a template rules
#    When we select the first "NETWORK_RATES" with data and run the build for that month via the API
#    Then we get the available brokers and clients details from the orders via the API
#    When I send a GET request "/api/template-rules/CLIENT/AGENCY/get-max-priority" to get max priority endpoint
#    And I send a POST request to the create TemplateRule without data table
#    And I check the template rules and make the created with highPriority
#    Then validate the generated Invoice as per the linked template for the orders via the API



