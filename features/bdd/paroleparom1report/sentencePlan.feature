Feature: Parole Report

  Background:
    Given that the Delius user is on the "Current sentence plan and response" page within the Parole Report

  Scenario: Delius user wants to enter details of the current sentence plan in the offender parole report

    Given that the Delius user wants to enter details of the current sentence plan in the offender parole report
    When  they enter the following information

      | Detail the prisoner`s current sentence plan objectives, including their response | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |

    Then this information should be saved in the report

  Scenario: Delius User does not enter any text into "Detail the prisoner`s current sentence plan objectives, including their response" free text field

    Given the user does not any enter any characters in the free text fields on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed

      | Detail the prisoner`s current sentence plan objectives, including their response | Enter the prisoner's current sentence plan objectives, including their response |

  Scenario: Delius user wants to continue writing the Parole Report

    Given they enter the following information

      | Detail the prisoner`s current sentence plan objectives, including their response | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Bibendum est ultricies integer quis.                                 |

    When  they select the "Continue" button
    Then  the user should be directed to the "Multi Agency Public Protection Arrangements (MAPPA)" UI

  Scenario: Delius user wants to leave the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
