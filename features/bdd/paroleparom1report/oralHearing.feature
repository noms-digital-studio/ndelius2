@Parom1
Feature: Parole Report

  Background:
    Given Delius user is on the "Oral hearing" UI on the Parole Report

  Scenario: Delius user does not enter any text into the "Oral hearing considerations" free text field

    Given the user does not any enter any characters in the free text fields on the page
    When they select the "Continue" button
    Then  the following error messages are displayed
      | Oral hearing considerations | Enter the oral hearing considerations |

  Scenario: Delius users wants more information to what they should include in the offender's parole report

    Given that the Delius user is unclear to what information they need to add to the "Oral hearing considerations" free text field
    When  they select "What to include" hyperlink
    Then  the UI should expand to show additional content to the end user

  Scenario: Delius user wants to continue writing the parole report

    Given they enter the following information
      | Oral hearing considerations | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
    When they select the "Continue" button
    Then the user should be directed to the "Sources" UI

  Scenario: Delius user wants to close the Parole Report

    When they select the "Close" button
    Then the user should be directed to the "Draft report saved" UI