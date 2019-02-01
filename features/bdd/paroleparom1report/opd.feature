Feature: Parole report - OPD Pathway

  Background:
    Given Delius user is on the "OPD Pathway" UI on the Parole Report

  Scenario: Delius user does not select an option on the 'OPD Pathway'

    Given that the user does not select an option on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Date of OPD screening                                                   | Enter the OPD screening date                                                      |
      | Has the prisoner been screened into the OPD pathway (OPD criteria met)? | Specify if the prisoner has been screened into the OPD pathway (OPD criteria met) |

  Scenario: Delius user enters a future date for the OPD screening date

    Given they enter the date "TOMORROW" for "Date of OPD screening"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Date of OPD screening | The OPD screening date must be in the past |

  Scenario: Delius User enters the OPD screening date which is before the conviction date

    When they enter the date "OVER_1_YEAR_AGO" for "Date of OPD screening"
    And they select the "Continue" button
    Then the following error messages are displayed
      | Date of OPD screening | The OPD screening date must be after the conviction date |

  Scenario: Delius user specifies that the prisoner has not been screened and wants to continue completing the parole report

    Given they enter the date "YESTERDAY" for "Date of OPD screening"
    And they select the "No" option on the "Has the prisoner been screened into the OPD pathway (OPD criteria met)?"
    When  they select the "Continue" button
    Then  the user should be directed to the "Behaviour in prison" UI

  Scenario: Delius user specifies that the prisoner has been screened but does not specify whether they have received consultation or a formulation

    Given they enter the date "YESTERDAY" for "Date of OPD screening"
    And they select the "Yes" option on the "Has the prisoner been screened into the OPD pathway (OPD criteria met)?"
    When they select the "Continue" button
    Then  the following error messages are displayed
      | Have you received consultation or a formulation? | Specify whether you have you received consultation or a formulation |

  Scenario: Delius user specifies that the prisoner has been screened and wants to continue completing the parole report

    Given they enter the date "YESTERDAY" for "Date of OPD screening"
    And they select the "Yes" option on the "Has the prisoner been screened into the OPD pathway (OPD criteria met)?"
    And they select the "Yes" option on the "Have you received consultation or a formulation?"
    When they select the "Continue" button
    Then the user should be directed to the "Behaviour in prison" UI

  Scenario: Delius user wants to leave the parole report
    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
