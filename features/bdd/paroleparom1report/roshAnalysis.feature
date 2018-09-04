Feature: Parole Report

  Background:
  Given Delius user is on the "RoSH analysis" UI on the Parole Report

  Scenario: Delius User wants to add details for offender's RoSH analysis within their parole Report without a risk of absconding

    When they enter the following information
      | Detail the nature of the risk of serious harm to all relevant groups              | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
      | What factors might increase the risk of serious harm to the relevant groups?      | Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat                  |
      | What factors might decrease the risk of serious harm to the relevant groups?      | Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.                      |
      | Analyse the likelihood of further offending                                       | Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum               |
    And they select the "No" option on the "Does the prisoner pose a risk of absconding?"
    When they select the "Continue" button
    Then the user should be directed to the "Risk Management Plan (RMP)" UI

  Scenario: Delius User wants to add details for offender's RoSH analysis within their parole Report with a risk of absconding

    When they enter the following information
      | Detail the nature of the risk of serious harm to all relevant groups              | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
      | What factors might increase the risk of serious harm to the relevant groups?      | Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat                  |
      | What factors might decrease the risk of serious harm to the relevant groups?      | Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.                      |
      | Analyse the likelihood of further offending                                       | Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum               |
    And they select the "Yes" option on the "Does the prisoner pose a risk of absconding?"
    And they enter the following information
      |  Provide details of the absconding risk | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
    When  they select the "Continue" button
    Then the user should be directed to the "Risk Management Plan (RMP)" UI

  Scenario: Delius User does not complete the relevant fields within the "RoSH Analysis" UI

    When  they select the "Continue" button
    Then  the following error messages are displayed
      |  Detail the nature of the risk of serious harm to all relevant groups               | Enter the nature of the risk of serious harm                                          |
      |  What factors might increase the risk of serious harm to the relevant groups?       | Enter the factors that might increase the risk of serious harm                        |
      |  What factors might decrease the risk of serious harm to the relevant groups?       | Enter the factors that might decrease the risk of serious harm                        |
      |  Analyse the likelihood of further offending                                        | Enter your analysis of the likelihood of further offending                            |
      |  Does the prisoner pose a risk of absconding?                                       | Specify if the prisoner poses a risk of absconding                                    |

  Scenario: Delius User completes the relevant fields within the "RoSH Analysis" UI, selects "Yes" option on the "Does the prisoner pose a risk of absconding?" but does not provide details of the absconding risk

    When they enter the following information
      | Detail the nature of the risk of serious harm to all relevant groups              | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
      | What factors might increase the risk of serious harm to the relevant groups?      | Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat                  |
      | What factors might decrease the risk of serious harm to the relevant groups?      | Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.                      |
      | Analyse the likelihood of further offending                                       | Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum               |
    And they select the "Yes" option on the "Does the prisoner pose a risk of absconding?"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      |  Provide details of the absconding risk                                           | Enter the details of the absconding risk |

  Scenario: Delius user wants to leave the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
