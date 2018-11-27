Feature: Short Format Pre-sentence Report - Offence analysis

  Background: Delius user is on the "Offence analysis" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Offence analysis" page within the Short Format Pre-sentence Report

  Scenario: Delius users wants more information to what they should include in the offender's report

    Given that the Delius user is unclear to what information they need to add to the "Offence analysis" free text field
    When  they select "What to include" hyperlink
    Then  the UI should expand to show additional content to the end user

  Scenario: Delius users wants more information to what they should include in the offender's report

    Given that the Delius user is unclear to what information they need to add to the "Patterns of offending behaviour (if applicable)" free text field
    When  they select "What to include" hyperlink
    Then  the UI should expand to show additional content to the end user

  Scenario: Delius user does not enter any text into the "Offence analysis" required fields

    Given the user does not any enter any characters in the free text fields on the page
    When they select the "Continue" button
    Then  the following error messages are displayed
      | Offence analysis | Enter your analysis of the offence |

  Scenario: Delius user completes all options on the "Offence analysis" UI

    When they enter the following information
      | Offence analysis                                | Some offence analysis text      |
      | Patterns of offending behaviour (if applicable) | Some patterns of offending text |

    Then the following information should be saved in the report
      | offenceAnalysis    | Some offence analysis text      |
      | patternOfOffending | Some patterns of offending text |

  Scenario: Delius user wants to continue writing the Short Format Pre-sentence Report
    Given Delius User completes the "Offence analysis" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Offender assessment" UI