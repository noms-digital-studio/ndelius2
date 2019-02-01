@SFR
Feature: Short Format Pre-sentence Report - Offender assessment

  Background: Delius user is on the "Offender assessment" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Offender assessment" page within the Short Format Pre-sentence Report

  Scenario: Delius user does not complete the "Offender assessment" required fields

    When they select the "Continue" button
    Then the following error messages are displayed
      | Accommodation                                                                                                                                | Select underlying issues from the options below                                 |
      | Is there evidence of the offender experiencing trauma?                                                                                       | Specify whether there is evidence of the offender experiencing trauma           |
      | Does the offender have caring responsibilities for children or adults, or have they ever had caring responsibilities for children or adults? | Specify whether the offender has caring responsibilities for children or adults |

  Scenario: Delius user specifies experience of trauma and caring responsibilities but does not complete the required fields

    Given they select the radio button with id "experienceTrauma_yes"
    And they select the radio button with id "caringResponsibilities_yes"
    When they select the "Continue" button
    Then the following error messages are displayed
      | Experience of trauma    | Enter the experience of trauma    |
      | Caring responsibilities | Enter the caring responsibilities |

  Scenario: Delius users wants more information to what they should include in the offender's report

    Given they select the radio button with id "experienceTrauma_yes"
    When that the Delius user is unclear to what information they need to add to the "Experience of trauma" free text field
    And they select "What to include" hyperlink
    Then the UI should expand to show additional content to the end user

  Scenario: Delius users wants more information to what they should include in the offender's report

    Given they select the radio button with id "caringResponsibilities_yes"
    When that the Delius user is unclear to what information they need to add to the "Caring responsibilities" free text field
    And they select "What to include" hyperlink
    Then the UI should expand to show additional content to the end user

  Scenario: Delius user completes all options on the "Offender assessment" UI

    Given that the "Accommodation" is ticked
    And that the "Employment, training and education" is ticked
    And that the "Finance" is ticked
    And that the "Relationships" is ticked
    And that the "Substance misuse" is ticked
    And that the "Physical & mental health" is ticked
    And that the "Thinking & behaviour" is ticked
    And that the "Other (Please specify below)" is ticked
    And they select the radio button with id "experienceTrauma_yes"
    And they select the radio button with id "caringResponsibilities_yes"
    When they enter the following information
      | Provide a brief assessment for accommodation            | Some accommodation text           |
      | Provide a brief assessment for employment               | Some employment text              |
      | Provide a brief assessment for finance                  | Some finance text                 |
      | Provide a brief assessment for relationships            | Some relationships text           |
      | Provide a brief assessment for substance misuse         | Some substance misuse text        |
      | Provide a brief assessment for physical & mental health | Some health text                  |
      | Provide a brief assessment for behaviour                | Some behaviour text               |
      | Provide a brief assessment of other issues              | Some other issue text             |
      | Experience of trauma                                    | Some trauma text                  |
      | Caring responsibilities                                 | Some caring responsibilities text |

    Then the following information should be saved in the report
      | issueAccommodation            | true                              |
      | issueEmployment               | true                              |
      | issueFinance                  | true                              |
      | issueRelationships            | true                              |
      | issueSubstanceMisuse          | true                              |
      | issueHealth                   | true                              |
      | issueBehaviour                | true                              |
      | issueOther                    | true                              |
      | issueAccommodationDetails     | Some accommodation text           |
      | issueEmploymentDetails        | Some employment text              |
      | issueFinanceDetails           | Some finance text                 |
      | issueRelationshipsDetails     | Some relationships text           |
      | issueSubstanceMisuseDetails   | Some substance misuse text        |
      | issueHealthDetails            | Some health text                  |
      | issueBehaviourDetails         | Some behaviour text               |
      | issueOtherDetails             | Some other issue text             |
      | experienceTraumaDetails       | Some trauma text                  |
      | caringResponsibilitiesDetails | Some caring responsibilities text |

  Scenario: Delius user wants to continue writing the Short Format Pre-sentence Report

    Given Delius User completes the "Offender assessment" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Risk assessment" UI