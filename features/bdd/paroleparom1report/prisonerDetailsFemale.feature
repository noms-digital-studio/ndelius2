Feature: Parole Report

  Background: Delius user is on the "Prisoner details" UI within the Parole Report
    Given that the Delius user is on the "Prisoner details" page within the Parole Report for a female prisoner

  Scenario: Delius user wants to enter details for Female prisoner whom has Indeterminate sentence

    Given that the delius user want to enter for Female prisoner who has Indeterminate sentence
    And they input the following information
      | Prison or Young Offender Institution | York       |
      | Prison number                        | P98793-123 |
    And they select the "Restricted" option on the "Current prison category"
    And they enter the following information
      | Offence  | Aggravated assault |
      | Sentence | 5 years            |
    And they select the "Indeterminate" option on the "Sentence type"
    And they input the following information
      | Tariff length | 5 years |
    And they enter the date "29/06/2019" for "Tariff expiry date"
    Then the following information should be saved in the prisoner parole report
      | prisonerDetailsPrisonInstitution | York               |
      | prisonerDetailsPrisonersFullName | Jane lizzy Doe     |
      | prisonerDetailsPrisonNumber      | P98793-123         |
      | prisonerDetailsNomisNumber       | F123456            |
      | prisonerDetailsPrisonersCategory | restricted         |
      | prisonerDetailsOffence           | Aggravated assault |
      | prisonerDetailsSentence          | 5 years            |
      | prisonerDetailsSentenceType      | indeterminate      |
      | prisonerDetailsTariffLength      | 5 years            |
      | prisonerDetailsTariffExpiryDate  | 29/06/2019         |

  Scenario: Delius user wants to enter details for Female prisoner whom has Determinate sentence

    Given that the delius user want to enter for Female prisoner who has Determinate sentence
    And they input the following information
      | Prison or Young Offender Institution | York       |
      | Prison number                        | P98793-123 |
    And they select the "A" option on the "Current prison category"
    And they enter the following information
      | Offence  | Aggravated assault |
      | Sentence | 20 years           |
    And they select the "Determinate" option on the "Sentence type"
    And they enter the date "08/12/2021" for "Parole eligibility date"
    Then the following information should be saved in the prisoner parole report
      | prisonerDetailsPrisonInstitution     | York               |
      | prisonerDetailsPrisonersFullName     | Jane lizzy Doe     |
      | prisonerDetailsPrisonNumber          | P98793-123         |
      | prisonerDetailsNomisNumber           | F123456            |
      | prisonerDetailsPrisonersCategory     | a                  |
      | prisonerDetailsOffence               | Aggravated assault |
      | prisonerDetailsSentence              | 20 years           |
      | prisonerDetailsSentenceType          | determinate        |
      | prisonerDetailsParoleEligibilityDate | 08/12/2021         |