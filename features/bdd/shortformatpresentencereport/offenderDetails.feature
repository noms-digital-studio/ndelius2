@SFR
Feature: Short Format Pre-sentence Report - Offender details

  Background: Delius user is on the "Offender details" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Offender details" page within the Short Format Pre-sentence Report

  Scenario: Delius user wants to continue writing the Short Format Pre-sentence Report

    Given Delius User completes the "Offender details" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Sentencing court details" UI