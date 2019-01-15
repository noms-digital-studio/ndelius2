Feature: Offender Summary - alerts

  Scenario: Offender has breached conviction conditions

    Given that the offender has breached conviction conditions
    When they navigate to the offender summary page
    Then they should not see a breached conditions alert


  Scenario: Offender has not breached conviction conditions

    Given that the offender has not breached any conditions
    When they navigate to the offender summary page
    Then they should not see a breached conditions alert
