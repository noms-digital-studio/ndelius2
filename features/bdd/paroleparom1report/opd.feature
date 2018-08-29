Feature: Parole report - OPD Pathway

  Background:
  Given Delius user is on the "OPD Pathway" UI on the Parole Report

  Scenario: User selects the 'yes' option on the 'OPD Pathway'

    When they select the "Yes" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    Then the screen should expand to show additional OPD Pathway content to the user

  Scenario: User selects the 'no' option on the 'OPD Pathway'

    When they select the "No" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    Then the screen should hide additional OPD Pathway content to the user


  Scenario: Delius user does not select an option on the 'OPD Pathway'

    Given that the user does not select an option on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Has the prisoner met the OPD screening criteria and been considered for OPD pathway services? | Specify if the prisoner has met OPD screening criteria and been considered for OPD pathway services |

  Scenario: Delius user wants to continue completing the parole report

    Given they select the "Yes" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    When  they select the "Continue" button
    Then  the user should be directed to the "Behaviour in prison" UI

  Scenario: Delius user wants to continue completing the parole report

    Given they select the "No" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    When  they select the "Continue" button
    Then  the user should be directed to the "Behaviour in prison" UI

  Scenario: Delius user wants to leave the parole report
    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
