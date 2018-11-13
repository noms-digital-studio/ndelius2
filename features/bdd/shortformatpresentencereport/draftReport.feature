Feature: Short Format Pre-sentence Report - Draft report saved

  Background: Delius user is on the "Offender details" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Offender details" page within the Short Format Pre-sentence Report

  Scenario: Delius user wants to close the Short Format Pre-sentence Report

    Given Delius User closes the Report
    Then the user should be directed to the "Draft report saved" UI