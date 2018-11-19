Feature: Parole Report - Signature & date

  Background:
    Given that the Delius user is on the "Prisoner details" page within the Parole Report
    And Delius User completes the "Prisoner details" UI within the Parole Report
    Then the user should be directed to the "Prisoner contact" UI
    And Delius User completes the "Prisoner contact" UI within the Parole Report
    Then the user should be directed to the "RoSH at point of sentence" UI
    And Delius User completes the "RoSH at point of sentence" UI within the Parole Report
    Then the user should be directed to the "Victims" UI
    And Delius User completes the "Victims" UI within the Parole Report
    Then the user should be directed to the "OPD pathway" UI
    And Delius User completes the "OPD Pathway" UI within the Parole Report
    Then the user should be directed to the "Behaviour in prison" UI
    And Delius User completes the "Behaviour in prison" UI within the Parole Report
    Then the user should be directed to the "Interventions" UI
    And Delius User completes the "Interventions" UI within the Parole Report
    Then the user should be directed to the "Current sentence plan and response" UI
    And Delius User completes the "Current sentence plan and response" UI within the Parole Report
    Then the user should be directed to the "Multi Agency Public Protection Arrangements (MAPPA)" UI
    And Delius User completes the "MAPPA" UI within the Parole Report
    Then the user should be directed to the "Current risk assessment scores" UI
    And Delius User completes the "Current risk assessment scores" UI within the Parole Report
    Then the user should be directed to the "Current RoSH: community" UI
    And Delius User completes the "Current RoSH community" UI within the Parole Report
    Then the user should be directed to the "Current RoSH: custody" UI
    And Delius User completes the "Current RoSH custody" UI within the Parole Report
    Then the user should be directed to the "Risk to the prisoner" UI
    And Delius User completes the "Risk to the prisoner" UI within the Parole Report
    Then the user should be directed to the "RoSH analysis" UI
    And Delius User completes the "RoSH analysis" UI within the Parole Report
    Then the user should be directed to the "Risk Management Plan (RMP)" UI
    And Delius User completes the "Risk Management Plan" UI within the Parole Report
    Then the user should be directed to the "Resettlement plan for release" UI
    And Delius User completes the "Resettlement plan for release" UI within the Parole Report
    Then the user should be directed to the "Supervision plan for release" UI
    And Delius User completes the "Supervision plan for release" UI within the Parole Report
    Then the user should be directed to the "Recommendation" UI
    And Delius User completes the "Recommendation" UI within the Parole Report
    Then the user should be directed to the "Oral hearing" UI
    And Delius User completes the "Oral hearing" UI within the Parole Report
    Then the user should be directed to the "Sources" UI
    And Delius User completes the "Sources" UI within the Parole Report
    Then the user should be directed to the "Check your report" UI
    And the button for "Prisoner details" must display "SAVED"
    And the button for "Prisoner contact" must display "SAVED"
    And the button for "RoSH at point of sentence" must display "SAVED"
    And the button for "Victims" must display "SAVED"
    And the button for "OPD pathway" must display "SAVED"
    And the button for "Behaviour in prison" must display "SAVED"
    And the button for "Interventions" must display "SAVED"
    And the button for "Current sentence plan" must display "SAVED"
    And the button for "MAPPA" must display "SAVED"
    And the button for "Current risk assessment" must display "SAVED"
    And the button for "Current RoSH: community" must display "SAVED"
    And the button for "Current RoSH: custody" must display "SAVED"
    And the button for "Risk to the prisoner" must display "SAVED"
    And the button for "RoSH analysis" must display "SAVED"
    And the button for "Risk Management Plan (RMP)" must display "SAVED"
    And the button for "Resettlement plan for release" must display "SAVED"
    And the button for "Supervision plan for release" must display "SAVED"
    And the button for "Recommendation" must display "SAVED"
    And the button for "Oral hearing" must display "SAVED"
    And the button for "Sources" must display "SAVED"
    And the button for "Signature & date" must display "NOT STARTED"
    And Delius User is ready to sign their Parole Report
    Then the user should be directed to the "Signature & date" UI

  Scenario: Delius user does not complete the relevant questions on the "Signature & date" UI

    When they select the "Submit" button
    Then the following error messages are displayed
      | Name                           | Enter the report author                  |
      | NPS Division and LDU           | Enter the NPS division and LDU           |
      | Office address                 | Enter the office address                 |
      | Email address                  | Enter the email address                  |
      | Telephone number and extension | Enter the telephone number and extension |
      | Completion date                | Enter the completion date                |

  Scenario: Delius User enters the completion date which is before the conviction date

    When they enter the date "OVER_1_YEAR_AGO" for "Completion date"
    And they select the "Submit" button
    Then the following error messages are displayed
      | Completion date | The completion date must be after the conviction date |

  Scenario: Delius user wants to sign and date their parole report WITHOUT counter signature

    When they input the following information
      | Name                 | Jane Doe           |
      | NPS Division and LDU | Stafford, Midlands |
    And they enter the following information into a classic TextArea
      | Office address | 4 Lichfield Road, Stafford ST17 4JX |
    And they input the following information
      | Email address                  | jane.doe@nps.gov.uk |
      | Telephone number and extension | 0124 5896456        |
    And they enter the date "07/08/2018" for "Completion date"

    Then the following information should be saved in the prisoner parole report
      | signatureName          | Jane Doe                            |
      | signatureDivision      | Stafford, Midlands                  |
      | signatureOfficeAddress | 4 Lichfield Road, Stafford ST17 4JX |
      | signatureEmail         | jane.doe@nps.gov.uk                 |
      | signatureTelephone     | 0124 5896456                        |
      | signatureDate          | 07/08/2018                          |

  Scenario: Delius user wants to sign and date their parole report WITH counter signature

    When they input the following information
      | Name                 | Jane Doe           |
      | NPS Division and LDU | Stafford, Midlands |
    And they enter the following information into a classic TextArea
      | Office address | 4 Lichfield Road, Stafford ST17 4JX |
    And they input the following information
      | Email address                  | jane.doe@nps.gov.uk |
      | Telephone number and extension | 0124 5896456        |
      | Name of countersignature       | Joe Bloggs          |
      | Role of countersignature       | SPO                 |
    And they enter the date "07/08/2018" for "Completion date"

    Then the following information should be saved in the prisoner parole report
      | signatureName          | Jane Doe                            |
      | signatureDivision      | Stafford, Midlands                  |
      | signatureOfficeAddress | 4 Lichfield Road, Stafford ST17 4JX |
      | signatureEmail         | jane.doe@nps.gov.uk                 |
      | signatureTelephone     | 0124 5896456                        |
      | signatureCounterName   | Joe Bloggs                          |
      | signatureCounterRole   | SPO                                 |
      | signatureDate          | 07/08/2018                          |

  Scenario: Delius user wants to submit their Parole report WITHOUT counter signature

    When they input the following information
      | Name                 | Jane Doe                            |
      | NPS Division and LDU | Stafford, Midlands                  |
      | Office address       | 4 Lichfield Road, Stafford ST17 4JX |
    And they enter the following information into a classic TextArea
      | Email address | jane.doe@nps.gov.uk |
    And they input the following information
      | Telephone number and extension | 0124 5896456 |
    And they enter the date "TODAY" for "Completion date"
    And they select the "Submit" button
    Then the user should be directed to the "Report saved" UI

  Scenario: Delius user wants to submit their Parole report WITH counter signature

    When they input the following information
      | Name                 | Jane Doe           |
      | NPS Division and LDU | Stafford, Midlands |
    And they enter the following information into a classic TextArea
      | Office address | 4 Lichfield Road, Stafford ST17 4JX |
    And they input the following information
      | Email address                  | jane.doe@nps.gov.uk |
      | Telephone number and extension | 0124 5896456        |
      | Name of countersignature       | Joe Bloggs          |
      | Role of countersignature       | SPO                 |
    And they enter the date "TODAY" for "Completion date"
    And they select the "Submit" button
    Then the user should be directed to the "Report saved" UI