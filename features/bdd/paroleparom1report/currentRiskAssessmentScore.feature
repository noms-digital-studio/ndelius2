Feature: Parole Report

  Background:
    Given Delius user is on the "Current risk assessment scores" UI on the Parole Report

  Scenario: Delius user add Current Risk Assessment Scores information to the offender's parole report

    Given they select the "Yes" option on the "Has a Risk Matrix 2000 assessment been completed?"
    And they select the radio button with id "riskAssessmentMatrix2000Score_very_high"
    And they select the "Yes" option on the "Has a Spousal Assault Risk Assessment (SARA) been completed?"
    And they select the radio button with id "riskAssessmentSpousalAssaultScore_high"
    And  they input the following information
      | Risk of serious recidivism (RSR) score                       | 6.32     |
      | OGRS3 probability of proven reoffending                      | 23       |
      | OGP probability of proven non violent type reoffending       | 23       |
      | OVP probability of proven violent type reoffending           | 24       |
    Then this information should be saved in the prisoner parole report
    And the following information should be saved in the prisoner parole report
      | riskAssessmentMatrix2000AssessmentCompleted     | yes   |
      | riskAssessmentMatrix2000Score                   | very_high   |
      | riskAssessmentSpousalAssaultAssessmentCompleted | yes   |
      | riskAssessmentSpousalAssaultScore               | high        |

  Scenario: User wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI

  Scenario: Delius user wants to continue writing the parole report

    Given they select the "Yes" option on the "Has a Risk Matrix 2000 assessment been completed?"
    And they select the radio button with id "riskAssessmentMatrix2000Score_very_high"
    And they select the "Yes" option on the "Has a Spousal Assault Risk Assessment (SARA) been completed?"
    And they select the radio button with id "riskAssessmentSpousalAssaultScore_high"
    And  they input the following information
      | Risk of serious recidivism (RSR) score                       | 6.32     |
      | OGRS3 probability of proven reoffending                      | 23       |
      | OGP probability of proven non violent type reoffending       | 23       |
      | OVP probability of proven violent type reoffending           | 24       |
    When  they select the "Continue" button
    Then  the user should be directed to the "Current RoSH: community" UI

  Scenario: Offender has not had a Risk Matrix and Spousal Assault Risk Assessment carried out on them

    Given they select the "No" option on the "Has a Risk Matrix 2000 assessment been completed?"
    And they select the "No" option on the "Has a Spousal Assault Risk Assessment (SARA) been completed?"
    When they input the following information
      | Risk of serious recidivism (RSR) score                       | 6.32 |
      | OGRS3 probability of proven reoffending                      | 23   |
      | OGP probability of proven non violent type reoffending       | 23   |
      | OVP probability of proven violent type reoffending           | 24   |
    Then this information should be saved in the prisoner parole report
    And the following information should be saved in the prisoner parole report
      | riskAssessmentMatrix2000AssessmentCompleted      | no   |
      | riskAssessmentSpousalAssaultAssessmentCompleted  | no   |


  Scenario: Delius user does not complete the relevant questions on the UI

    Given that the user enters no information on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Risk of serious recidivism (RSR) score                       | Enter the RSR score                              |
      | OGRS3 probability of proven reoffending                      | Enter the OGRS3 score                            |
      | OGP probability of proven non violent type reoffending       | Enter the OGP score                              |
      | OVP probability of proven violent type reoffending           | Enter the OVP score                              |
      | Has a Risk Matrix 2000 assessment been completed?            | Specify if a Risk Matrix 2000 has been completed |
      | Has a Spousal Assault Risk Assessment (SARA) been completed? | Specify if a SARA has been completed             |

  Scenario: Delius user does not complete all the relevant questions on the UI

    Given they select the "Yes" option on the "Has a Risk Matrix 2000 assessment been completed?"
    And they select the "Yes" option on the "Has a Spousal Assault Risk Assessment (SARA) been completed?"
    And  they input the following information
      | Risk of serious recidivism (RSR) score                       | 6.32      |
      | OGRS3 probability of proven reoffending                      | 23        |
      | OGP probability of proven non violent type reoffending       | 23        |
      | OVP probability of proven violent type reoffending           | 24        |
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Risk Matrix 2000                       | Select the Risk Matrix 2000 score |
      | Spousal Assault Risk Assessment (SARA) | Select the SARA score             |
