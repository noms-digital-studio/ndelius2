Feature: Parole Report - Prisoner contact

  Background:
    Given Delius User is on the "MAPPA" UI within the Parole Report

  Scenario: Delius user does not specify if the prisoner is eligible for MAPPA

    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Is the prisoner eligible for MAPPA? | Specify if the prisoner is eligible for MAPPA |

  Scenario: Delius user specifies that the prisoner is eligible for MAPPA but does not enter any information

    Given they select the radio button with id "eligibleForMappa_yes"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | When was the prisoner screened for MAPPA (MAPPA Q completed)? | Enter the date when the prisoner was screened for MAPPA |
      | What is the prisoner`s current MAPPA category? | Select the prisoner's current MAPPA category |
      | What is the prisoner`s current MAPPA level? | Select the prisoner's current MAPPA level |

  Scenario: Delius user wants to enter the Multi Agency Public Protection Arrangements (MAPPA) information

    Given they select the radio button with id "eligibleForMappa_yes"
    When they enter the date "YESTERDAY" for "When was the prisoner screened for MAPPA (MAPPA Q completed)?"
    And they select the radio button with id "mappaCategory_1"
    And they select the radio button with id "mappaLevel_2"
    When they select the "Continue" button
    Then the user should be directed to the "Current risk assessment scores" UI

  Scenario: Delius user specifies a future date for the MAPPA Q completion date

    Given they select the radio button with id "eligibleForMappa_yes"
    And they enter the date "TOMORROW" for "When was the prisoner screened for MAPPA (MAPPA Q completed)?"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | When was the prisoner screened for MAPPA (MAPPA Q completed)? | The MAPPA date must be at least 6 months prior to the Parole Eligibility date and must be in the past |

  Scenario: Delius user specifies prisoner is not eligible for MAPPA

    Given they select the radio button with id "eligibleForMappa_no"
    When they select the "Continue" button
    Then the user should be directed to the "Current risk assessment scores" UI

  Scenario: Delius user wants to close the report

    When they select the "Close" button
    Then the user should be directed to the "Draft report saved" UI
