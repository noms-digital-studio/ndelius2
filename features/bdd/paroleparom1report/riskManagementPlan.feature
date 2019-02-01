@Parom1
Feature: Parole Report

  Background:
    Given Delius user is on the "Risk Managemant Plan RMP" UI on the Parole Report

  Scenario: Delius user wants to leave the "Risk Management Plan (RMP)" page without entering any details into the free text fields

    When they select the "Yes" option on the "Does the prisoner require a community RMP?"
    And they select the "Continue" button
    Then  the following error messages are displayed
      | Current situation                     | Enter the current situation                     |
      | Supporting agencies                   | Enter the supporting agencies                   |
      | Support                               | Enter the support                               |
      | Control                               | Enter the control                               |
      | Added measures for specific risks     | Enter the added measures for specific risks     |
      | Agency actions                        | Enter the agency actions                        |
      | Additional conditions or requirements | Enter the additional conditions or requirements |
      | Level of contact                      | Enter the level of contact                      |
      | Contingency plan                      | Enter the contingency plan                      |

  Scenario: Delius user wants to continue populating the Parole Report with information

    When they select the "Yes" option on the "Does the prisoner require a community RMP?"
    And they enter the following information
      | Current situation                     | Some current situation text                     |
      | Supporting agencies                   | Some supporting agencies text                   |
      | Support                               | Some support text                               |
      | Control                               | Some control text                               |
      | Added measures for specific risks     | Some added measures for specific risks text     |
      | Agency actions                        | Some agency actions text                        |
      | Additional conditions or requirements | Some additional conditions or requirements text |
      | Level of contact                      | Some level of contact text                      |
      | Contingency plan                      | Some contingency plan text                      |
    Then the following information should be saved in the report
      | currentSituation     | Some current situation text                     |
      | supportingAgencies   | Some supporting agencies text                   |
      | support              | Some support text                               |
      | control              | Some control text                               |
      | riskMeasures         | Some added measures for specific risks text     |
      | agencyActions        | Some agency actions text                        |
      | additionalConditions | Some additional conditions or requirements text |
      | levelOfContact       | Some level of contact text                      |
      | contingencyPlan      | Some contingency plan text                      |

    When they select the "Continue" button
    Then the user should be directed to the "Resettlement plan for release" UI

  Scenario: Delius user specifies that the prisoner does not require a community RMP

    When they select the "No" option on the "Does the prisoner require a community RMP?"
    And they select the "Continue" button
    Then the user should be directed to the "Resettlement plan for release" UI

  Scenario: Delius user wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI