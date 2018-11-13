Feature: Short Format Pre-sentence Report - Sign your report

  Background:
    Given that the Delius user is on the "Offender details" page within the Short Format Pre-sentence Report
    And Delius User completes the "Offender details" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Sentencing court details" UI
    And Delius User completes the "Sentencing court details" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Offence details" UI
    And Delius User completes the "Offence details" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Offence analysis" UI
    And Delius User completes the "Offence analysis" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Offender assessment" UI
    And Delius User completes the "Offender assessment" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Risk assessment" UI
    And Delius User completes the "Risk assessment" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Proposal" UI
    And Delius User completes the "Proposal" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Sources of information" UI
    And Delius User completes the "Sources of information" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Check your report" UI

    And the button for "Offender details" must display "SAVED"
    And the button for "Sentencing court details" must display "SAVED"
    And the button for "Offence details" must display "SAVED"
    And the button for "Offence analysis" must display "SAVED"
    And the button for "Offender assessment" must display "SAVED"
    And the button for "Risk assessment" must display "SAVED"
    And the button for "Proposal" must display "SAVED"
    And the button for "Sources of information" must display "SAVED"
    And Delius User is ready to sign their Short Format Pre-sentence Report
    Then the user should be directed to the "Sign your report" UI

  Scenario: Delius user does not complete the relevant questions on the "Sign your report" UI

    When they select the "Submit" button
    Then the following error messages are displayed
      | Report author | Enter the report author |
      | Office        | Enter the office        |

  Scenario: Delius user wants to sign and date their Short Format Pre-sentence Report

    When they input the following information
      | Report author   | Jane Doe           |
      | Office          | Stafford, Midlands |
      | Completion date | 07/08/2018         |

    Then the following information should be saved in the report
      | reportAuthor | Jane Doe           |
      | office       | Stafford, Midlands |
      | reportDate   | 07/08/2018         |

  Scenario: Delius user wants to submit their Short Format Pre-sentence Report

    When they input the following information
      | Report author   | Jane Doe           |
      | Office          | Stafford, Midlands |
      | Completion date | 07/08/2018         |

    And they select the "Submit" button
    Then the user should be directed to the "Report saved" UI