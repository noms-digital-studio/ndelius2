@SFR
Feature: Short Format Pre-sentence Report - Proposal

  Background: Delius user is on the "Proposal" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Proposal" page within the Short Format Pre-sentence Report

  Scenario: Delius user does not complete the "Proposal" required fields

    When they select the "Continue" button
    Then the following error messages are displayed
      | Enter a proposed sentence | Enter your proposed sentence |

  Scenario: Delius users wants more information to what they should include in the offender's report

    Given that the Delius user is unclear to what information they need to add to the "Enter a proposed sentence" free text field
    And they select "What to include" hyperlink
    Then the UI should expand to show additional content to the end user

  Scenario: Delius user confirms that they have considered equality and diversity information

    Given that the user selects "yes" or "no" radio buttons
    Then they can continue to enter a proposed sentence


  Scenario: Delius user completes all options on the "Risk assessment" UI

    Given they enter the following information
      | Enter a proposed sentence | Some proposed sentence text |

    Then the following information should be saved in the report
      | proposal | Some proposed sentence text |

  Scenario: Delius user wants to continue writing the Short Format Pre-sentence Report

    Given Delius User completes the "Proposal" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Sources of information" UI
