Feature: Offender Summary - risks and warnings


Scenario: Kieron Robinson is not on any registers and warnings list within Delius

  Given that the Kieron Robinson is not on any registers and warning lists within Delius
  And they navigate to the offender summary page
  When  the Delius user selects the "Registers and warnings" link on the "Offender Summary" UI
  Then  they should see the following alert and registration text "no data"

Scenario: Offender is on the "Low" RoSH Register in Delius
  Given that the following alert and registration information is saved for an offender in Delius
  | Flag   | Type      |  Date       | Colour |
  | RoSH   | Low RoSH  | 07/12/2018  | Green  |
  And they navigate to the offender summary page
  When  the Delius user selects the "Registers and warnings" link on the "Offender Summary" UI
  Then  then they should see the following alert and registrations information
  | Type           | Status word  | Status Colour     |Description        | Date        |
  | RoSH           | Low          | Green             |Low RoSH           | 07/12/2018  |

Scenario: Offender is on multiple registers and warnings lists within Delius
  Given that the following alert and registration information is saved for an offender in Delius
  | Flag                | Type                             |  Date       | Colour |
  | Public Protection   | MAPPA                            | 12/12/2016  | Red    |
  | RoSH                | Very High RoSH                   | 07/12/2018  | Red    |
  | Safeguarding        | Risk to Children                 | 10/01/2015  | Red    |
  | Alerts              | Lifer                            | 06/12/2018  | Red    |
  | Information         | Duplicate Offender Records Exist | 06/12/2018  | White  |
  And they navigate to the offender summary page
  When  the Delius user selects the "Registers and warnings" link on the "Offender Summary" UI
  Then  then they should see the following alert and registrations information
  | Type               | Status word  | Status Colour     |Description                      | Date        |
  | Alerts             | High         | Red               |Lifer                            | 06/12/2018  |
  | Information        | Warning      | White             |Duplicate Offender Records Exist | 06/12/2018  |
  | Public Protection  | High         | Red               |MAPPA                            | 12/12/2016  |
  | RoSH               | Very High    | Red               |Very High RoSH                   | 07/12/2018  |
  | Safeguarding       | High         | Red               |Risk to Children                 | 10/01/2015  |

Scenario: Offender is on multiple categories within Registers and warnings Lists within Delius
  Given that the following alert and registration information is saved for an offender in Delius
  | Flag                | Type                             |  Date       | Colour |
  | Public Protection   | MAPPA                            | 12/12/2016  | Red    |
  | RoSH                | Very High RoSH                   | 07/12/2018  | Red    |
  | Safeguarding        | Risk to Children                 | 10/01/2015  | Red    |
  | Alerts              | Lifer                            | 06/12/2018  | Red    |
  | Public Protection   | Risk to Public                   | 06/12/2018  | Amber  |
  | Information         | Duplicate Offender Records Exist | 06/12/2018  | White  |
  | Public Protection   | Street Gangs                     | 06/12/2018  | Amber  |
  | Public Protection   | Registered sex offender          | 06/12/2018  | Red    |
  | Safeguarding        | Child Concerns                   | 06/12/2018  | Amber  |
  | Information         | Home Office Interest             | 06/12/2018  | Amber  |
  | Public Protection   | Risk to Prisoner                 | 06/12/2018  | Amber  |
  | Information         | Warrant/Summons                  | 06/12/2018  | Amber  |
  | Alerts              | Restraining Order                | 06/12/2018  | Amber  |
  | Public Protection   | Risk to Staff                    | 06/12/2018  | Red    |
  | Information         | Record to be retained            | 06/12/2018  | Green  |
  And they navigate to the offender summary page
  When  the Delius user selects the "Registers and warnings" link on the "Offender Summary" UI
  Then  then they should see the following alert and registrations information
  | Type               | Status word          | Status Colour     |Description                      | Date        |
  | Alerts             | High                 | Red               |Lifer                            | 06/12/2018  |
  | Alerts             | Medium               | Amber             |Restraining Order                | 06/12/2018  |
  | Information        | Medium               | Amber             |Home Office Interest             | 06/12/2018  |
  | Information        | Medium               | Amber             |Warrant/Summons                  | 06/12/2018  |
  | Information        | Low                  | Green             |Record to be retained            | 06/12/2018  |
  | Information        | Warning              | White             |Duplicate Offender Records Exist | 06/12/2018  |
  | Public Protection  | High                 | Red               |MAPPA                            | 12/12/2016  |
  | Public Protection  | High                 | Red               |Registered sex offender          | 06/12/2018  |
  | Public Protection  | High                 | Red               |Risk to Staff                    | 06/12/2018  |
  | Public Protection  | Medium               | Amber             |Risk to Prisoner                 | 06/12/2018  |
  | Public Protection  | Medium               | Amber             |Risk to Public                   | 06/12/2018  |
  | Public Protection  | Medium               | Amber             |Street Gangs                     | 06/12/2018  |
  | RoSH               | Very High            | Red               |Very High RoSH                   | 07/12/2018  |
  | Safeguarding       | High                 | Red               |Risk to Children                 | 10/01/2015  |
  | Safeguarding       | Medium               | Amber             |Child Concerns                   | 06/12/2018  |

  Scenario: Offender has a serious registration

    Given that the a registration which is serious is saved for an offender in Delius
    And they navigate to the offender summary page
    And the Delius user selects the "Registers and warnings" link on the "Offender Summary" UI
    When offender registrations are displayed
    Then the serious registration message "This offender has serious registrations" is displayed

  Scenario: Offender has no serious registration

    Given that the a registration which is not serious is saved for an offender in Delius
    And they navigate to the offender summary page
    And the Delius user selects the "Registers and warnings" link on the "Offender Summary" UI
    When offender registrations are displayed
    Then the serious registration message is not displayed
