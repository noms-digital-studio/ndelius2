Feature: Parole Report - RoSH at point of sentence

  Background:
    Given Delius user is on the "RoSH at point of sentence" UI on the Parole Report

  Scenario: Delius User does not complete any section on the page

    When they select the "Continue" button
    Then the following error messages are displayed

      | Was a RoSH assessment completed at the point of sentence? | Specify if a RoSH assessment was completed at the point of sentence |
      | What is the prisoner`s attitude to the index offence? | Enter the prisoner's attitude to the index offence  |
      | What is the prisoner`s attitude to their previous offending? | Enter the prisoner's attitude to their previous offending  |

  Scenario: Delius User wants to add details for RoSH at point of sentence WITHOUT a completed assessment and they do not complete the required fields

    When they select the "No" option on the "Was a RoSH assessment completed at the point of sentence?"
    And they select the "Continue" button
    Then the following error messages are displayed

      | What is the prisoner`s attitude to the index offence? | Enter the prisoner's attitude to the index offence  |
      | What is the prisoner`s attitude to their previous offending? | Enter the prisoner's attitude to their previous offending  |

  Scenario: Delius User wants to add details for RoSH at point of sentence WITH a completed assessment and they do not complete the required fields

    When they select the "Yes" option on the "Was a RoSH assessment completed at the point of sentence?"
    And they select the "Continue" button
    Then the following error messages are displayed

      | When was the RoSH assessment completed? | Enter the date when the RoSH assessment was completed |
      | Public | Select the risk to the public |
      | Known adult | Select the risk to any known adult |
      | Children | Select the risk to children |
      | Prisoners | Select the risk to prisoners |
      | Staff | Select the risk to staff |
      | What is the prisoner`s attitude to the index offence? | Enter the prisoner's attitude to the index offence  |
      | What is the prisoner`s attitude to their previous offending? | Enter the prisoner's attitude to their previous offending  |

  Scenario: Delius User wants to add details for RoSH at point of sentence WITHOUT a completed assessment

    When they select the "No" option on the "Was a RoSH assessment completed at the point of sentence?"
    And they enter the following information

      | What is the prisoner`s attitude to the index offence?        | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
      | What is the prisoner`s attitude to their previous offending? | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |

    When they select the "Continue" button
    Then the user should be directed to the "Victims" UI

  Scenario: Delius User wants to add details for RoSH at point of sentence WITH a completed assessment

    When Delius User completes the "RoSH at point of sentence" UI within the Parole Report
    Then the user should be directed to the "Victims" UI

  Scenario: Delius user wants to leave the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI