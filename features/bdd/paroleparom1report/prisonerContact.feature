Feature: Parole Report - Prisoner contact

  Background:
    Given that the Delius user is on the "Prisoner contact" page within the Parole Report

  Scenario: Delius user wants to enter all the contact details that an offender manager has had with a prisoner, their family and prison staff

    Given that the Delius user wants to enter all the contact details that an offender manager has had with a prisoner, their family and prison staff
    When they enter the following information

      | How long have you managed the prisoner, and what contact have you had with them?     | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Bibendum est ultricies integer quis.                                 |
      | What contact have you had with the prisoner`s family, partners or significant others? | Mauris cursus mattis molestie a iaculis at. Ullamcorper a lacus vestibulum sed arcu non odio euismod lacinia. Ullamcorper morbi tincidunt ornare massa.                                          |
      | What contact have you had with other relevant agencies about the prisoner?  | Enim blandit volutpat maecenas volutpat blandit aliquam etiam. Et malesuada fames ac turpis egestas sed. Elementum tempus egestas sed sed risus. Vestibulum lorem sed risus ultricies tristique. |

    Then this information should be saved in the prisoner parole report


  Scenario: Delius user wants to leave the "Offender manager: prisoner contact" UI without putting any details in the free text fields

    Given the user does not any enter any characters in the free text fields on the page
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | How long have you managed the prisoner, and what contact have you had with them? | Enter how long you have managed the prisoner, and what contact you have had with them |
      | What contact have you had with the prisoner`s family, partners or significant others? | Enter what contact you have had with the prisoner's family, partners or significant others |
      | What contact have you had with other relevant agencies about the prisoner? | Enter what contact you have had with other relevant agencies about the prisoner |

  Scenario: Delius user wants to continue entering Prisoner details in the Parole report

    Given they enter the following information

      | How long have you managed the prisoner, and what contact have you had with them?     | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Bibendum est ultricies integer quis.                                 |
      | What contact have you had with the prisoner`s family, partners or significant others? | Mauris cursus mattis molestie a iaculis at. Ullamcorper a lacus vestibulum sed arcu non odio euismod lacinia. Ullamcorper morbi tincidunt ornare massa.                                          |
      | What contact have you had with other relevant agencies about the prisoner?  | Enim blandit volutpat maecenas volutpat blandit aliquam etiam. Et malesuada fames ac turpis egestas sed. Elementum tempus egestas sed sed risus. Vestibulum lorem sed risus ultricies tristique. |
    When  they select the "Continue" button
    Then  the user should be directed to the "ROSH at point of sentence" UI

  Scenario: Delius user wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
