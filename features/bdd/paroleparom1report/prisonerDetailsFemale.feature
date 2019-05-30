@Parom1
Feature: Parole Report

  Background: Delius user is on the "Prisoner details" UI within the Parole Report
    Given the prisoner named "Jane Doe" has a valid NOMS number in NOMIS where he is known as "Jane Doe"
    And the prisoner has a category of "Q, Fem Restricted"
    And that the Delius user is on the "Prisoner details" page within the Parole Report for a female prisoner

  Scenario: Delius user wants to enter details for Female prisoner whom has Indeterminate sentence

    Given that the delius user want to enter for Female prisoner who has Indeterminate sentence
    And they enter the following information
      | Sentence length | 5 years |
    And they select the "Yes" option on the "Does the prisoner have an indeterminate sentence?"
    And they input the following information
      | Tariff length | 5 years |
    And they enter the date "29/06/2019" for "Tariff expiry date"
    Then the following information should be saved in the report
      | prisonerDetailsPrisonInstitution | HMP Humber                                                                                         |
      | prisonerDetailsPrisonersFullName | Jane Doe                                                                                           |
      | prisonerDetailsPrisonNumber      | LH5058                                                                                             |
      | prisonerDetailsNomisNumber       | F123456                                                                                            |
      | prisonerDetailsPrisonersCategory | restricted                                                                                         |
      | prisonerDetailsOffence           | Stealing the limelight - 08/11/2018<br>Interrupting - 07/07/2017<br>Jumping the queue - 06/06/2016 |
      | prisonerDetailsSentence          | 5 years                                                                                            |
      | prisonerDetailsSentenceType      | indeterminate                                                                                      |
      | prisonerDetailsTariffLength      | 5 years                                                                                            |
      | prisonerDetailsTariffExpiryDate  | 29/06/2019                                                                                         |

  Scenario: Delius user wants to enter details for Female prisoner whom has Determinate sentence

    Given that the delius user want to enter for Female prisoner who has Determinate sentence
    And they select the "Closed" option on the "Current prison category"
    And they enter the following information
      | Sentence length | 20 years |
    And they select the "No" option on the "Does the prisoner have an indeterminate sentence?"
    And they select the "Extended" option on the "Sentence type"
    And they enter the date "08/12/2021" for "Parole eligibility date"
    Then the following information should be saved in the report
      | prisonerDetailsPrisonInstitution       | HMP Humber                                                                                         |
      | prisonerDetailsPrisonersFullName       | Jane Doe                                                                                           |
      | prisonerDetailsPrisonNumber            | LH5058                                                                                             |
      | prisonerDetailsNomisNumber             | F123456                                                                                            |
      | prisonerDetailsPrisonersCategory       | closed                                                                                             |
      | prisonerDetailsOffence                 | Stealing the limelight - 08/11/2018<br>Interrupting - 07/07/2017<br>Jumping the queue - 06/06/2016 |
      | prisonerDetailsSentence                | 20 years                                                                                           |
      | prisonerDetailsSentenceType            | determinate                                                                                        |
      | prisonerDetailsDeterminateSentenceType | extended                                                                                           |
      | prisonerDetailsParoleEligibilityDate   | 08/12/2021                                                                                         |