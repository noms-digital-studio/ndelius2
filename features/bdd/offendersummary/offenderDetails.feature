Feature: Offender summary - offender details

  Scenario: Delius user views complete offender details

    Given that the offender has the following data from json file "offender" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    Then the page should display the following by class name
      | qa-aliases     | Yes (1)       |
      | qa-gender      | Male          |
      | qa-ni-number   | AB123456C     |
      | qa-nationality | British       |
      | qa-ethnicity   | White British |
      | qa-interpreter | Yes           |
      | qa-disability  | --            |

  Scenario: Delius user views offender details with no data

    Given that the offender has the following data from json file "offenderMissingData" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    Then the page should display the following by class name
      | qa-aliases     | No      |
      | qa-gender      | Unknown |
      | qa-ni-number   | Unknown |
      | qa-nationality | Unknown |
      | qa-ethnicity   | Unknown |
      | qa-interpreter | Unknown |

  Scenario: Delius user views offender who has a main address and contact details registered

    Given that the offender has the following data from json file "offender" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    And they expand the "Contact details" content section
    And they expand the "Main address" content section
    Then the page should display the following by class name
      | qa-main-address-1 | 5 Sea View               |
      | qa-main-address-2 | High Street, Nether Edge |
      | qa-main-address-3 | Sheffield                |
      | qa-main-address-4 | Yorkshire                |
      | qa-main-address-5 | S10 1EQ                  |
      | qa-telephone      | 01753862474              |
      | qa-email          | Brian.Findus@session.com |
      | qa-mobile         | 07777123456              |

  Scenario: Delius user views offender who does not have a main address or contact details registered

    Given that the offender has the following data from json file "offenderMissingData" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    And they expand the "Contact details" content section
    And they expand the "Main address" content section
    Then the page should display the following by class name
      | qa-main-address-none | No main address |
      | qa-telephone         | Unknown         |
      | qa-email             | Unknown         |
      | qa-mobile            | Unknown         |

  Scenario: Delius user views offender who has a main address registered as no fixed abode

    Given that the offender has the following data from json file "offenderMainAddressNFA" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    And they expand the "Main address" content section
    Then the page should display the following by class name
      | qa-main-address-nfa | No fixed abode |
