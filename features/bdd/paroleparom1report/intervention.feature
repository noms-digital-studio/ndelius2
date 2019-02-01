@Parom1
Feature: Parole report Intervention UI

  Background:
    Given that the Delius user is on the "Interventions" page within the Parole Report

  Scenario: Delius user wants to enter intervention details for a prisoner in his parole report

    Given they want to enter the intervention details for a prisoner
    When  they enter the following information
      | Detail the interventions the prisoner has completed | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.|
      | Interventions summary| Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. |
    Then this information should be saved in the report


  Scenario: Delius user wants to leave the "Interventions" screen without putting any details in the free text fields

    Given the user does not any enter any characters in the free text fields on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Detail the interventions the prisoner has completed | Detail the interventions the prisoner has completed |
      | Interventions summary| Enter the interventions summary |

  Scenario: Delius user wants to continue entering Prisoner details in the Parole report

    Given that the Delius user has entered details into "Detail the interventions the prisoner has completed" and "Interventions summary" field
    When  they select the "Continue" button
    Then  the user should be directed to the "Prison sentence plan and response" UI

  Scenario: Delius user wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
