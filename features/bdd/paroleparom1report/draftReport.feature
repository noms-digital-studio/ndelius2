Feature: Parole Report - Draft report saved

  Background: Delius user is on the "Prisoner details" UI
    Given that the Delius user is on the "Prisoner details" page within the Parole Report

  Scenario: Delius user wants to close the Parole Report

    Given Delius User closes the Report
    Then the user should be directed to the "Draft report saved" UI