@Parom1
Feature: Parole Report

  Background:
    Given Delius user is on the "Risk Managemant Plan RMP" UI on the Parole Report

  Scenario: Delius user wants to leave the "Risk Management Plan (RMP)" page without entering any details into the free text fields

    When they select the "Yes" option on the "Does the prisoner require a community RMP?"
    And they select the "Continue" button
    Then  the following error messages are displayed
      | Current situation         | Enter the current situation         |
      | Supervision               | Enter the supervision               |
      | Monitoring / Control      | Enter the monitoring / control      |
      | Interventions / Treatment | Enter the interventions / treatment |
      | Victim safety planning    | Enter the victim safety planning    |
      | Contingency plan          | Enter the contingency plan          |

  Scenario: Delius user wants to continue populating the Parole Report with information

    When they select the "Yes" option on the "Does the prisoner require a community RMP?"
    And they enter the following information
      | Current situation         | Some current situation text         |
      | Supervision               | Some supervision text               |
      | Monitoring / Control      | Some monitoring / control text      |
      | Interventions / Treatment | Some interventions / treatment text |
      | Victim safety planning    | Some victim safety planning text    |
      | Contingency plan          | Some contingency plan text          |
    Then the following information should be saved in the report
      | currentSituation       | Some current situation text         |
      | supervision            | Some supervision text               |
      | monitoringControl      | Some monitoring / control text      |
      | interventionsTreatment | Some interventions / treatment text |
      | victimSafetyPlanning   | Some victim safety planning text    |
      | contingencyPlan        | Some contingency plan text          |

    When they select the "Continue" button
    Then the user should be directed to the "Resettlement plan for release" UI

  Scenario: Delius user specifies that the prisoner does not require a community RMP

    When they select the "No" option on the "Does the prisoner require a community RMP?"
    And they select the "Continue" button
    Then the user should be directed to the "Resettlement plan for release" UI

  Scenario: Delius user wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI