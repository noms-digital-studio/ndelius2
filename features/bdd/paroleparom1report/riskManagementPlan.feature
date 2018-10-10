Feature: Parole Report

  Background:
  Given Delius user is on the "Risk Managemant Plan RMP" UI on the Parole Report

  Scenario: Delius user wants to leave the "Risk Management Plan (RMP)" page without entering any details into the free text fields

    When they select the "Yes" option on the "Does the prisoner require a community RMP?"
    And they select the "Continue" button
    Then  the following error messages are displayed
      | Current situation (Agencies)             | Enter the current situation (Agencies)             |
      | Support                                  | Enter the support                                  |
      | Control                                  | Enter the control                                  |
      | Added measures for specific risks        | Enter the added measures for specific risks        |
      | Agency actions                           | Enter the agency actions                           |
      | Additional conditions or requirements    | Enter the additional conditions or requirements    |
      | Level of contact                         | Enter the level of contact                         |
      | Contingency plan                         | Enter the contingency plan                         |

  Scenario: Delius user wants to continue populating the Parole Report with information

    When they select the "Yes" option on the "Does the prisoner require a community RMP?"
    And they enter the following information
      | Current situation (Agencies)             | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.                                    |
      | Support                                  | Pharetra pharetra massa massa ultricies mi.                                                                                                                    |
      | Control                                  | Aenean euismod elementum nisi quis eleifend quam adipiscing vitae proin.                                                                                       |
      | Added measures for specific risks        | Varius sit amet mattis vulputate enim nulla. Nulla facilisi morbi tempus iaculis urna id volutpat.                                                             |
      | Agency actions                           | Placerat vestibulum lectus mauris ultrices eros in cursus. Hendrerit gravida rutrum quisque non tellus orci. Molestie ac feugiat sed lectus vestibulum mattis. |
      | Additional conditions or requirements    | Ultricies mi eget mauris pharetra et ultrices neque ornare. Amet risus nullam eget felis eget nunc lobortis mattis aliquam.                                    |
      | Level of contact                         | Ipsum dolor sit amet consectetur adipiscing elit duis tristique sollicitudin. Vestibulum lorem sed risus ultricies                                             |
      | Contingency plan                         | Hendrerit dolor magna eget est lorem ipsum dolor sit amet. Laoreet suspendisse interdum consectetur libero id.                                                 |
    When they select the "Continue" button
    Then the user should be directed to the "Resettlement plan for release" UI

  Scenario: Delius user specifies that the prisoner does not require a community RMP

    When they select the "No" option on the "Does the prisoner require a community RMP?"
    And they select the "Continue" button
    Then the user should be directed to the "Resettlement plan for release" UI

  Scenario: Delius user wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI