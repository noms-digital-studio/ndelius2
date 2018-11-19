Feature: Parole Report Landing Page


  Scenario: Prisoner has a valid NOMIS number in NOMIS

    Given the prisoner named "Keiran Dobson" has a valid NOMS number in NOMIS where he is known as "Keiron Dobson"
    When the user is on the Parole Report landing page
    Then the user must see an image of the prisoner
    And  the user must see the prisoner name "Keiron Dobson"
    And  the user must see the "Start now" button


  Scenario: Delius user wants to write a Parole report for a prisoner

    Given that the user is on the Parole Report landing page
    When they select the "Start now" button on the landing page
    Then the user should be directed to the "Prisoner details" UI


  Scenario: Prisoner does not have a NOMIS number in Delius

    Given the prisoner named "Keiran Dobson" has no NOMS number
    When the user is on the Parole Report landing page
    Then the user must see the prisoner name "Keiran Dobson"
    And the user must see the message "Add the prisoner's NOMS number to Delius"
    And the user must not see the "Start now" button

  Scenario: Prisoner does not have a Valid NOMIS number in NOMIS

    Given the prisoner named "Keiran Dobson" has a NOMS number that matches no prisoner
    When the user is on the Parole Report landing page
    Then the user must see the prisoner name "Keiran Dobson"
    And the user must see the message "Update the prisoner's NOMS number in Delius"
    And the user must not see the "Start now" button


  Scenario: The connection for NOMIS is down

    Given the connection for NOMIS API is not working
    When the user is on the Parole Report landing page
    Then the user must see the message "Unfortunately, we cannot connect you to the prisoner's information at the moment"
    And the user must not see the "Start now" button
