Feature: Parole Report - Supervision plan for release UI

Background:
  Given Delius User is on the "Supervision plan for release" UI

Scenario: Delius user wants to enter Resettlement details for an offender within their Parole report

  Given they select the "Yes" option on the "Does the prisoner require a supervision plan for release?"
  And  they enter the following information
    | Detail the supervision plan for release  | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
  Then this information should be saved in the report
  And the following information should be saved in the report
    | supervisionPlanRequired        | yes  |

Scenario: User does not select an option on the "Supervision plan for release" UI

  Given that the user enters no information on the page
  When  they select the "Continue" button
  Then  the following error messages are displayed
    | Does the prisoner require a supervision plan for release? | Specify if the prisoner requires a supervision plan for release |

Scenario: User selects yes but enters no detail on the "Supervision plan for release" UI

  Given they select the "Yes" option on the "Does the prisoner require a supervision plan for release?"
  When  they select the "Continue" button
  Then  the following error messages are displayed
    | Detail the supervision plan for release | Enter the supervision plan for release |

Scenario: User wants to close the report

  When  they select the "Close" button
  Then  the user should be directed to the "Draft report saved" UI

Scenario: User wants to continuing writing the parole report

  Given they select the "No" option on the "Does the prisoner require a supervision plan for release?"
  When  they select the "Continue" button
  Then  the user should be directed to the "Recommendation" UI
