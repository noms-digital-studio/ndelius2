@SFR
Feature: Short Format Pre-sentence Report - Sources of information

  Background: Delius user is on the "Sources of information" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Sources of information" page within the Short Format Pre-sentence Report

  Scenario: Delius user specifies other source but does not enter any text into the required field

    Given that the "Other (please specify below)" is ticked
    When they select the "Continue" button
    Then  the following error messages are displayed
      | Other source(s) of information | Enter the other information source details |

  Scenario: Delius user completes all options on the "Sources of information" UI

    Given that the "Interview" is ticked
    And that the "Service records" is ticked
    And that the "CPS summary" is ticked
    And that the "Previous OASys assessments" is ticked
    And that the "Previous convictions" is ticked
    And that the "Victim statement" is ticked
    And that the "Safeguarding checks" is ticked
    And that the "Police information" is ticked
    And that the "Sentencing guidelines" is ticked
    And that the "Domestic abuse call out information" is ticked
    And that the "Equality Information Form " is ticked
    And that the "Other (please specify below)" is ticked
    When they enter the following information
      | Other source(s) of information | Some other sources of information text |

    Then the following information should be saved in the report
      | interviewInformationSource                      | true                                   |
      | serviceRecordsInformationSource                 | true                                   |
      | cpsSummaryInformationSource                     | true                                   |
      | oasysAssessmentsInformationSource               | true                                   |
      | previousConvictionsInformationSource            | true                                   |
      | victimStatementInformationSource                | true                                   |
      | childrenServicesInformationSource               | true                                   |
      | policeInformationSource                         | true                                   |
      | sentencingGuidelinesInformationSource           | true                                   |
      | domesticAbuseInformationSource                  | true                                   |
      | equalityInformationFormInformationSource        | true                                   |
      | otherInformationSource                          | true                                   |
      | otherInformationDetails                         | Some other sources of information text |

  Scenario: Delius user wants to continue writing the Short Format Pre-sentence Report

    Given Delius User completes the "Sources of information" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Check your report" UI
