Feature: Parole Report

  Background: Delius user is on the "Prisoner details" UI within the Parole Report
    Given that the Delius user is on the "Prisoner details" page within the Parole Report

  Scenario: Delius user wants to continue writing the Parole report

    Given that the Delius user has completed all the relevant fields within the "Prisoner details" UI
    When  they select the "Continue" button
    Then  the user should be directed to the "Prisoner contact" UI

  Scenario: Delius user wants to enter details for Male prisoner whom has Indeterminate sentence

    Given that the delius user want to enter for Male prisoner who has Indeterminate sentence
    And they select the "A" option on the "Current prison category"
    And they enter the following information
      | Sentence | 4 years            |
    And they select the "Indeterminate" option on the "Sentence type"
    And they input the following information
      | Tariff length | 5 years |
    And they enter the date "29/06/2019" for "Tariff expiry date"
    Then the following information should be saved in the prisoner parole report
      | prisonerDetailsPrisonInstitution | HMP Humber         |
      | prisonerDetailsPrisonersFullName | Jimmy Jammy Fizz   |
      | prisonerDetailsPrisonNumber      | LH5058             |
      | prisonerDetailsNomisNumber       | M123456            |
      | prisonerDetailsPrisonersCategory | a                  |
      | prisonerDetailsOffence           | Stealing the limelight (code123) - 08/11/2018 |
      | prisonerDetailsSentence          | 4 years            |
      | prisonerDetailsSentenceType      | indeterminate      |
      | prisonerDetailsTariffLength      | 5 years            |
      | prisonerDetailsTariffExpiryDate  | 29/06/2019         |


  Scenario: Delius user wants to enter details for Male prisoner whom has Determinate sentence

    Given that the delius user want to enter for Male prisoner who has Determinate sentence
    And they select the "C" option on the "Current prison category"
    And they enter the following information
      | Sentence | 20 years           |
    And they select the "Determinate" option on the "Sentence type"
    And they enter the date "08/11/2031" for "Parole eligibility date"
    And they enter the date "09/12/2031" for "Automatic release date/non parole eligibility date"
    Then the following information should be saved in the prisoner parole report
      | prisonerDetailsPrisonInstitution     | HMP Humber         |
      | prisonerDetailsPrisonersFullName     | Jimmy Jammy Fizz   |
      | prisonerDetailsPrisonNumber          | LH5058             |
      | prisonerDetailsNomisNumber           | M123456            |
      | prisonerDetailsPrisonersCategory     | c                  |
      | prisonerDetailsOffence               | Stealing the limelight (code123) - 08/11/2018 |
      | prisonerDetailsSentence              | 20 years           |
      | prisonerDetailsSentenceType          | determinate        |
      | prisonerDetailsParoleEligibilityDate | 08/11/2031         |
      | prisonerDetailsAutoReleaseDate       | 09/12/2031         |


  Scenario: Delius user wants to close the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI

  Scenario: Delius user does not complete all the relevant fields on the UI including Sentence type

    Given they enter the following information
      | Offence  | |
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Current prison category              | Select the current prison category             |
      | Offence                              | Enter the offence                              |
      | Sentence                             | Enter the sentence                             |
      | Sentence type                        | Select the sentence type                       |

  Scenario: Delius user does not complete all the relevant fields on the UI for an offender whom has indeterminate sentence

    Given they select the "Indeterminate" option on the "Sentence type"
    And they enter the following information
      | Offence  | |
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Current prison category              | Select the current prison category             |
      | Offence                              | Enter the offence                              |
      | Sentence                             | Enter the sentence                             |
      | Tariff length                        | Enter the tariff length                        |
      | Tariff expiry date                   | Enter the tariff expiry date                   |

  Scenario: Delius user does not complete all the relevant fields on the UI for an offender whom has Determinate sentence

    Given they select the "Determinate" option on the "Sentence type"
    And they enter the following information
      | Offence  | |
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Current prison category              | Select the current prison category             |
      | Offence                              | Enter the offence                              |
      | Sentence                             | Enter the sentence                             |
