Feature: Parole Report

  Background: Delius user is on the "Prisoner details" UI within the Parole Report
    Given the prisoner named "Jimmy Fizz" has a valid NOMS number in NOMIS where he is known as "Jimmy Fizz"
    And that the Delius user is on the "Prisoner details" page within the Parole Report

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
    Then the following information should be saved in the report
      | prisonerDetailsPrisonInstitution | HMP Humber         |
      | prisonerDetailsPrisonersFullName | Jimmy Fizz         |
      | prisonerDetailsPrisonNumber      | LH5058             |
      | prisonerDetailsNomisNumber       | M123456            |
      | prisonerDetailsPrisonersCategory | a                  |
      | prisonerDetailsOffence           | <p>Stealing the limelight - 08/11/2018</p><p>Interrupting - 07/07/2017</p><p>Jumping the queue - 06/06/2016</p> |
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
    Then the following information should be saved in the report
      | prisonerDetailsPrisonInstitution     | HMP Humber         |
      | prisonerDetailsPrisonersFullName     | Jimmy Fizz         |
      | prisonerDetailsPrisonNumber          | LH5058             |
      | prisonerDetailsNomisNumber           | M123456            |
      | prisonerDetailsPrisonersCategory     | c                  |
      | prisonerDetailsOffence               | <p>Stealing the limelight - 08/11/2018</p><p>Interrupting - 07/07/2017</p><p>Jumping the queue - 06/06/2016</p> |
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
      | Offence                              | Enter the offence                              |
      | Sentence                             | Enter the sentence                             |
      | Sentence type                        | Select the sentence type                       |

  Scenario: Delius user does not complete all the relevant fields on the UI for an offender whom has indeterminate sentence

    Given they select the "Indeterminate" option on the "Sentence type"
    And they enter the following information
      | Offence  | |
    When  they select the "Continue" button
    Then  the following error messages are displayed
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
      | Offence                              | Enter the offence                              |
      | Sentence                             | Enter the sentence                             |

  Scenario: Delius user enters dates in the past for Parole Eligibility and Automatic release date / non parole eligibility date

    Given they select the "Determinate" option on the "Sentence type"
    And they enter the date "YESTERDAY" for "Parole eligibility date"
    And they enter the date "YESTERDAY" for "Automatic release date/non parole eligibility date"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Parole eligibility date | The parole eligibility date must be in the future |
      | Automatic release date/non parole eligibility date | The automatic release date/non parole eligibility date must be in the future |

  Scenario: Delius user enters a date in the past for Tariff expiry

    Given they select the "Indeterminate" option on the "Sentence type"
    And they enter the date "YESTERDAY" for "Tariff expiry date"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Tariff expiry date | The tariff expiry date must be in the future |