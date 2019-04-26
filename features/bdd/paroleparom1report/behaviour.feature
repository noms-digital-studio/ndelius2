@Parom1
Feature: Parole report

  Background:
    Given that the Delius user is on the "Behaviour in prison" page within the Parole Report

  Scenario: Delius user wants to enter details of the offender's behaviour in Prison in the offender parole report

    Given that the Delius user wants to enter details of the offender's behaviour in Prison in the offender parole report
    When  they enter the following information
      | Detail the prisoner`s behaviour whilst in prison | Some behaviour whilst in prison text. |
      | RoTL summary                                     | Some RoRTL summary text.              |

    Then the following information should be saved in the report
      | behaviourDetail | Some behaviour whilst in prison text. |
      | rotlSummary     | Some RoRTL summary text.              |

  Scenario: Delius user wants to leave the "Behaviour in Prison" page without entering any details into the "Detail the prisoner`s behaviour whilst in prison" free text fields

    Given the user does not any enter any characters in the free text fields on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Detail the prisoner`s behaviour whilst in prison | Enter details of the prisoner's behaviour in prison |
      | RoTL summary                                     | Enter the RoTL summary                              |

  Scenario: Delius user wants to continue populating the Parole Report with information

    Given that the Delius user has entered details into "Detail the prisoner`s behaviour whilst in prison" and "RoTL summary" field
    When  they select the "Continue" button
    Then  the user should be directed to the "Interventions" UI

  Scenario: Delius user wants to leave the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
