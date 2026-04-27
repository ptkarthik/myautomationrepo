Feature: Create Client API
@Regression
  Scenario: Validate Client Retrieval
    Given I fetch client details with following query parameters
      | name                      | 22NW               |
      | traderColumn              | ORIGINATOR            |
      | uncommissionedVolumeTypes | PAIRS |
      | billingMethodsString      | VOLUME                |
      | active                    | No                  |
      | caspianClient             | No                 |
    Then I validate the response status code is 200
    And I validate the client details


  Scenario: Verify user is able to create a new client
    Given user create client with following details
      | name                      | 12              |
      | traderColumn              | ORIGINATOR            |
      | uncommissionedVolumeTypes | PAIRS |
      | billingMethodsString      | VOLUME                |
      | active                    | No                  |
      | caspianClient             | No                 |
    Then I validate the response status code is 201
    And I validate the client details
    When I send a delete request to the endpoint "api/clients/{id}"
    Then I should receive a response with status code 204