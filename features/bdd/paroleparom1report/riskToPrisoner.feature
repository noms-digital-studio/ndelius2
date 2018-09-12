Feature: Parole Report

  Background:
    Given Delius user is on the "Risk to the prisoner" UI on the Parole Report

  Scenario: Delius user does not complete all the fields within the "Risk to the prisoner" UI

    When they select the "Continue" button
    Then  the following error messages for each field are displayed
      | selfHarmCommunity   | Specify if the prisoner poses a risk of self harm in the community |
      | selfHarmCustody     | Specify if the prisoner poses a risk of self harm in custody |
      | othersHarmCommunity | Specify if the prisoner is at risk of serious harm from others in the community |
      | othersHarmCustody   | Specify if the prisoner is at risk of serious harm from others in custody |

  Scenario: Delius user wants to add "Risk to the prisoner" details to the offender's Parole Report

    Given they select the radio button with id "selfHarmCommunity_yes"
    And they select the radio button with id "selfHarmCustody_no"
    And they select the radio button with id "othersHarmCommunity_yes"
    And they select the radio button with id "othersHarmCustody_no"
    When they select the "Continue" button
    Then the user should be directed to the "RoSH analysis" UI
    
  Scenario: Delius user wants to leave the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
