Feature: Parole report - OPD Pathway

  Background:
    Given Delius user is on the "OPD Pathway" UI on the Parole Report

  Scenario: Delius user does not select an option on the 'OPD Pathway'

    Given that the user does not select an option on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Has the prisoner met the OPD screening criteria and been considered for OPD pathway services? | Specify if the prisoner has met OPD screening criteria and been considered for OPD pathway services |

  Scenario: Delius user specifies that the prisoner has been screened but does not enter the OPD screening date

    Given they select the "Yes" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Date of OPD screening | Enter the OPD screening date |

  Scenario: Delius user specifies that the prisoner has been screened but enters a future date for the OPD screening date

    Given they select the "Yes" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    And they enter the date "TOMORROW" for "Date of OPD screening"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Date of OPD screening | The OPD screening date must be in the past |

  Scenario: Delius User specifies that the prisoner has been screened but enters the OPD screening date which is before the conviction date

    Given they select the "Yes" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    When they enter the date "OVER_1_YEAR_AGO" for "Date of OPD screening"
    And they select the "Continue" button
    Then the following error messages are displayed
      | Date of OPD screening | The OPD screening date must be after the conviction date |

  Scenario: Delius user specifies that the prisoner has been screened and wants to continue completing the parole report

    Given they select the "Yes" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    And they enter the date "YESTERDAY" for "Date of OPD screening"
    When  they select the "Continue" button
    Then  the user should be directed to the "Behaviour in prison" UI

  Scenario: Delius user specifies that the prisoner has not been screened and wants to continue completing the parole report

    Given they select the "No" option on the "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?"
    And they enter the following information
      | Detail the reasons why the prisoner has not been screened including when it will happen | Some reason for not screening the prisoner text |
    When  they select the "Continue" button
    Then  the user should be directed to the "Behaviour in prison" UI

  Scenario: Delius user wants to leave the parole report
    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
