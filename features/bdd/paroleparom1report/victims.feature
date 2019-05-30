@Parom1
Feature:Parole Report - Victims

  Background:
    Given that the Delius user is on the "Victims" page within the Parole Report


  Scenario: Delius user wants to enter details of the victims in the parole Report

    When they select the "Yes" option on the "Are the victims engaged in the Victim Contact Scheme (VCS)?"
    And they select the "No" option on the "Do the victims wish to submit a Victim Personal Statement (VPS)?"
    And they enter the date "YESTERDAY" for "On what date did you contact the VLO?"
    And  they enter the following information

      | Analyse the impact of the offence on the victims | Some analysis of the impact of the offence on the victims |

    Then the following information should be saved in the report

      | victimsEngagedInVCS   | yes                                                                                                                                                                                     |
      | victimsSubmitVPS      | no                                                                                                                                                                                      |
      | victimsImpactDetails  | Some analysis of the impact of the offence on the victims |

  Scenario: Delius user wants to leave the "Victims" page without entering any details into the "Analyse the impact of the offence on the victims" field

    Given that the user enters no information on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed

      | Analyse the impact of the offence on the victims                 | Enter your analysis of the impact of the offence on the victims |
      | On what date did you contact the VLO?                            | Enter the date you contacted the VLO                            |
      | Are the victims engaged in the Victim Contact Scheme (VCS)?      | Specify if the victims are engaged with the VCS                 |
      | Do the victims wish to submit a Victim Personal Statement (VPS)? | Specify if the victims wish to submit a VPS                     |

  Scenario: Delius User enters the VLO contact date which is more than 1 year ago

    When they enter the date "OVER_1_YEAR_AGO" for "On what date did you contact the VLO?"
    And they select the "Continue" button
    Then the following error messages are displayed

      | On what date did you contact the VLO? | The VLO date must be within the last year |

  Scenario: Delius User enters the VLO contact date which is within the required range but is before the conviction date

    When they enter the date "OVER_6_MONTHS_AGO" for "On what date did you contact the VLO?"
    And they select the "Continue" button
    Then the following error messages are displayed

      | On what date did you contact the VLO? | The VLO date must be after the conviction date |

  Scenario: Delius user wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI

  Scenario: Delius user wants to continue entering Prisoner details in the Parole report

    Given they select the "No" option on the "Are the victims engaged in the Victim Contact Scheme (VCS)?"
    And they select the "No" option on the "Do the victims wish to submit a Victim Personal Statement (VPS)?"
    And they enter the date "YESTERDAY" for "On what date did you contact the VLO?"
    And  they enter the following information

      | Analyse the impact of the offence on the victims | Some details |

    When  they select the "Continue" button
    Then  the user should be directed to the "OPD pathway" UI

  Scenario: Delius user selects "Yes" to 'Do the victims wish to submit a Victim Personal Statement'

    When they select the "Yes" option on the "Do the victims wish to submit a Victim Personal Statement (VPS)?"
    Then the screen should expand to show additional VPS content to the user

  Scenario: Delius user selects "No" to 'Do the victims wish to submit a Victim Personal Statement'

    When they select the "No" option on the "Do the victims wish to submit a Victim Personal Statement (VPS)?"
    Then the screen should hide additional VPS content to the user

  Scenario: Delius user selects "Don`t know" to 'Do the victims wish to submit a Victim Personal Statement'

    When they select the "Don`t know" option on the "Do the victims wish to submit a Victim Personal Statement (VPS)?"
    Then the screen should hide additional VPS content to the user
