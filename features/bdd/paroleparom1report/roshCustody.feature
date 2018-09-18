Feature: Parole Report

  Background:
    Given Delius user is on the "Current RoSH custody" UI on the Parole Report

  Scenario: Delius user wants to enter RoSH community data for an offender within their parole report

    Given they select the radio button with id "roshCustodyPublic_low"
    And they select the radio button with id "roshCustodyKnownAdult_medium"
    And they select the radio button with id "roshCustodyChildren_high"
    And they select the radio button with id "roshCustodyPrisoners_very_high"
    And they select the radio button with id "roshCustodyStaff_low"
    When they select the "Continue" button
    Then the user should be directed to the "Risk to the prisoner" UI

  Scenario: Delius User does not complete the relevant fields within the "Current RoSH custody" UI

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