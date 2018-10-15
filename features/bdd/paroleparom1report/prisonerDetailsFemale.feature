Feature: Parole Report

  Background: Delius user is on the "Prisoner details" UI within the Parole Report
    Given that the Delius user is on the "Prisoner details" page within the Parole Report for a female prisoner

  Scenario: Delius user wants to enter details for Female prisoner whom has Indeterminate sentence

    Given that the delius user want to enter for Female prisoner who has Indeterminate sentence
    And they input the following information
      | Prison or Young Offender Institution | York     |
      | Prisoner`s full name                 | Jane Doe |
      | Prison number                        | P98793-123    |
      | NOMIS number                         | N2124214-3423 |
    And they select the "Restricted" option on the "Current prison category"
    And they enter the following information
      | Offence  | Aggravated assault |
      | Sentence | 5 years            |
    And they select the "Indeterminate" option on the "Sentence type"
    And they input the following information
      | Tariff length | 5 years |
    And they enter the date "29/06/2019" for "Tariff expiry date"
    Then this information should be saved in the prisoner parole report

  Scenario: Delius user wants to enter details for Female prisoner whom has Determinate sentence

    Given that the delius user want to enter for Female prisoner who has Determinate sentence
    And they input the following information
      | Prison or Young Offender Institution | York     |
      | Prisoner`s full name                 | Jane Doe |
      | Prison number                        | P98793-123    |
      | NOMIS number                         | N2124214-3423 |
    And they select the "A" option on the "Current prison category"
    And they enter the following information
      | Offence  | Aggravated assault |
      | Sentence | 20 years            |
    And they select the "Determinate" option on the "Sentence type"
    And they enter the date "08/12/2021" for "Parole eligibility date"
    Then this information should be saved in the prisoner parole report
