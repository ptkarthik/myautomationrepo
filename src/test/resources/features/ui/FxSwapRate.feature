Feature: FX Swap Rates CRUD Operations
  Verify the positive flow for Create, Read, Update, and Delete actions on FX Swap Rates.

  Background:
    Given The user is in the login page of Billing application
    When The user enters valid credentials as "user", "user"
    Given I navigate to the "FX Swap Rates" page
    And the "FX Swap Rates" page loads successfully

  Scenario: Add a new FX Swap Rate
    Given I open the "Add New Rate Rule" modal
    When I enter valid values in the following fields:
      | Tenor          | string2  |
      | Day Count Lower| 912    |
      | Day Count Upper| 915  |
      | Revenue Share  | 0.007|
      | Max Upper Enabled  | false|
    Then validate If the entries provided are existing already and save

  Scenario: Edit an existing FX Swap Rate
    Given I locate the row with "Tenor" as "O/N1123" and "Day Count" as "819 - 820"
    When I click the "Edit" icon for that row
    And I update the "Revenue Share" field to "0.09866"
    And I click "Save"
    Then the updated row reflects the following values:
      | Tenor  | DayCount | Revenue Share |
      | O/N1123   | 819 - 820    | 0.09866         |

  Scenario: Delete an FX Swap Rate
    Given I locate the row with "Tenor" as "ss to seven" and "Day Count" as "391 - 393"
    When I click the "Delete" icon for that row
    And I confirm the deletion
    Then the row no longer exists in the FX Swap Rates table