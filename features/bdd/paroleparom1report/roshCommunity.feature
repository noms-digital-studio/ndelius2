Feature: Parole Report

  Background:
    Given Delius user is on the "Current RoSH community" UI on the Parole Report

  Scenario: Delius user wants to enter RoSH community data for an offender within their parole report

    Given they select the radio button with id "roshCommunityPublic_low"
    And they select the radio button with id "roshCommunityKnownAdult_medium"
    And they select the radio button with id "roshCommunityChildren_high"
    And they select the radio button with id "roshCommunityPrisoners_very_high"
    And they select the radio button with id "roshCommunityStaff_low"
    When they select the "Continue" button
    Then the user should be directed to the "Current ROSH: custody" UI

  Scenario: Delius User does not complete the relevant fields within the "RoSH Analysis" UI

    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Public | Select the risk to the public |
      | Known adult | Select the risk to any known adult |
      | Children | Select the risk to children |
      | Prisoners | Select the risk to prisoners |
      | Staff | Select the risk to staff |

  Scenario: Delius user wants to leave the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI