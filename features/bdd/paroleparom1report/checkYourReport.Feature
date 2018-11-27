Feature: Parole Report

  Scenario: Delius user has completed all the relevant fields for "Victims" UI

    Given Delius User completes the "Victims" UI within the Parole Report
    When  they are select "Check your report" link on the navigation menu
    Then  the button for "Victims" must display "SAVED"

  Scenario: Delius user has not completed all the relevant fields for "Prisoner Contact" UI

    Given that the Delius user has not completed all the relevant fields for "Prisoner Contact" UI
    When  they are select "Check your report" link on the navigation menu
    Then  the button for "Prisoner contact" must display "INCOMPLETE"

  Scenario: Delius user has not completed any fields in the "Behaviour in Prison" UI

    Given that the Delius user has not completed any fields in the "Behaviour in Prison" UI
    When  they are select "Check your report" link on the navigation menu
    Then  the button for "Behaviour in prison" must display "NOT STARTED"

  Scenario: Delius user wants to complete the fields within the "Prisoner Contact"

    Given the Delius user is on "Check your report" UI
    And  they want to complete the fields within the "Prisoner Contact"
    When they select "Prisoner contact" hyperlink from the UI
    Then they must be directed to "Prisoner contact" UI
