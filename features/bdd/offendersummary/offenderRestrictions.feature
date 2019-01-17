Feature: Offender Summary - restricted access

  Scenario: Offender is on the exclusion list

    Given that the user is on the exclusion list for the offender with an exclusion message of "Sorry, you are excluded"
    When they attempt to navigate to the offender summary page
    Then they should not see an limited access message of "Sorry, you are excluded"


  Scenario: Offender is not on the restricted list

    Given that the user is not the restricted list for the offender with an restricted message of "Sorry, you are not included"
    When they attempt to navigate to the offender summary page
    Then they should not see an limited access message of "Sorry, you are not included"
