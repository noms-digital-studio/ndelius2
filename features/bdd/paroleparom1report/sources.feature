Feature: Parole Report - Sources UI

  Background:
  Given Delius user is on the "Sources" UI within the Parole Report

  Scenario: Delius user wants to enter information regarding the sources that they have used for the Parole Report

    Given that the "Judges comments" is ticked
    And   that the "Previous convictions" is ticked
    And they select the "Yes" option on the "Has any information not been made available to you, or are there any limitations to the sources?"
    When  they enter the following information
      | List all of the reports, assessments and directions you have used for this PAROM 1  | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
      | Provide an explanation                                                              | Pharetra pharetra massa massa ultricies mi. Aenean euismod elementum nisi quis eleifend quam adipiscing vitae proin.        |
    Then this information should be saved in the prisoner parole report
    And the following information should be saved in the prisoner parole report
      | sourcesPreviousConvictions        | true  |
      | sourcesCPSDocuments               | false |
      | sourcesJudgesComments             | true  |
      | sourcesParoleDossier              | false |
      | sourcesProbationCaseRecord        | false |
      | sourcesPrisonSecurityInformation  | false |
      | sourcesOther                      | false |
      | sourceLimitations                 | yes   |

  Scenario: Delius user selects "Other" options for case documents

    Given that the "Other" is ticked
    When  they don't enter text into the "Please enter the names of the other case documents"
    And  they select the "Continue" button
    Then  the following error messages are displayed
      | Detail any other case documents you have used | Enter the other case documents you have used |

  Scenario: Delius user does not complete all the fields within the "Sources" UI

    Given that the user enters no information on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | List all of the reports, assessments and directions you have used for this PAROM 1               | Enter the reports, assessments and directions you have used |
      | Has any information not been made available to you, or are there any limitations to the sources? | Specify if there have been any omissions or limitations     |
      | Previous convictions                                                                             | Select the case documents you have used                     |

  Scenario: Delius user selects "Yes" options for "Has any information not been made available to you, or are there any limitations to the sources"

    Given they select the "Yes" option on the "Has any information not been made available to you, or are there any limitations to the sources?"
    When  they don't enter text into the "Provide an explanation"
    And   they select the "Continue" button
    Then  the following error messages are displayed
      | Provide an explanation | Enter the explanation |

  Scenario: Delius user wants to know what information they should include in "List all the reports, assessments and directions you have used for this PAROM 1" free text field

    Given that the Delius user is unclear to what information they need to add to the "List all of the reports, assessments and directions you have used for this PAROM 1" free text field
    When  they select "What to include" hyperlink
    Then  the UI should expand to show additional content to the end user

  Scenario: Delius user wants to close the Parole Report

  Scenario: Delius user wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI

  Scenario: Delius user wants to continuing writing the Parole Report

    Given that the "Judges comments" is ticked
    And they select the "Yes" option on the "Has any information not been made available to you, or are there any limitations to the sources?"
    And  they enter the following information
      | List all of the reports, assessments and directions you have used for this PAROM 1  | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
      | Provide an explanation                                                              | Pharetra pharetra massa massa ultricies mi. Aenean euismod elementum nisi quis eleifend quam adipiscing vitae proin.        |
    When  they select the "Continue" button
    Then  the user should be directed to the "Check your report" UI
