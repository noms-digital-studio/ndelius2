Feature: Short Format Pre-sentence Report - Offence details

  Background: Delius user is on the "Offence details" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Offence details" page within the Short Format Pre-sentence Report

  Scenario: Delius user does not complete the "Offence details" required fields

    Given they enter the following information
      | Main offence and date |  |
    When they select the "Continue" button
    Then the following error messages are displayed
      | Main offence and date        | Enter the main offence and date      |
      | Brief summary of the offence | Enter a brief summary of the offence |

  Scenario: Delius user completes all options on the "Offence details" UI

    When they enter the following information
      | Main offence and date                    | Some main offence and date text        |
      | Other offences and dates (if applicable) | Some other offences and date text      |
      | Brief summary of the offence             | Some brief summary of the offence text |

    Then the following information should be saved in the report
      | mainOffence    | Some main offence and date text        |
      | otherOffences  | Some other offences and date text      |
      | offenceSummary | Some brief summary of the offence text |

  Scenario: Delius user wants to continue writing the Short Format Pre-sentence Report

    Given Delius User completes the "Offence details" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Offence analysis" UI