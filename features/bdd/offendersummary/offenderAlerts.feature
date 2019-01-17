Feature: Offender Summary - alerts

  Scenario: Offender has breached conviction conditions

    Given that the offender has breached conviction conditions
    When they navigate to the offender summary page
    Then they should not see a breached conditions alert


  Scenario: Offender has not breached conviction conditions

    Given that the offender has not breached any conditions
    When they navigate to the offender summary page
    Then they should not see a breached conditions alert


  Scenario: Offender has very high RoSH registration

    Given that the following alert and registration information is saved for an offender in Delius
      | Flag | Type           | Date       | Colour |
      | RoSH | Very High RoSH | 07/12/2018 | Red    |
    When they navigate to the offender summary page
    Then they should see a "very high" RoSH registration alert
    And the RoSH registration alert should be "Red"

  Scenario: Offender has high RoSH registration

    Given that the following alert and registration information is saved for an offender in Delius
      | Flag | Type      | Date       | Colour |
      | RoSH | High RoSH | 07/12/2018 | Red    |
    When they navigate to the offender summary page
    Then they should see a "high" RoSH registration alert
    And the RoSH registration alert should be "Red"

  Scenario: Offender has medium RoSH registration

    Given that the following alert and registration information is saved for an offender in Delius
      | Flag | Type        | Date       | Colour |
      | RoSH | Medium RoSH | 07/12/2018 | Amber  |
    When they navigate to the offender summary page
    Then they should see a "medium" RoSH registration alert
    And the RoSH registration alert should be "Amber"

  Scenario: Offender has low RoSH registration

    Given that the following alert and registration information is saved for an offender in Delius
      | Flag | Type     | Date       | Colour |
      | RoSH | Low RoSH | 07/12/2018 | Green  |
    When they navigate to the offender summary page
    Then they should see a "low" RoSH registration alert
    And the RoSH registration alert should be "Green"

  Scenario: Offender does not have a RoSH registration

    Given that the following alert and registration information is saved for an offender in Delius
      | Flag  | Type | Date       | Colour |
      | Other | Low  | 07/12/2018 | Green  |
    When they navigate to the offender summary page
    Then they should not see a RoSH registration alert