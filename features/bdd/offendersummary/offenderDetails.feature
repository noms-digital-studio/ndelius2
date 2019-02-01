@OffenderSummary
Feature: Offender summary - offender details

  Scenario: Delius user views complete offender details

    Given that the offender has the following data from json file "offender" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    Then the page should display the following by class name
      | qa-aliases      | Yes (1)       |
      | qa-middle-names | Isaac, Kweku  |
      | qa-gender       | Male          |
      | qa-ni-number    | AB123456C     |
      | qa-nationality  | British       |
      | qa-ethnicity    | White British |
      | qa-interpreter  | Yes           |
      | qa-disability   | No Disability |

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
      | qa-disability  | Unknown |
    And the page should not display the following by class name
      | qa-middle-names |

  Scenario: Delius user views offender who has a main address and contact details registered

    Given that the offender has the following data from json file "offender" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    And they expand the "Contact details" content section
    Then the page should display the following by class name
      | qa-telephone      | 01753862474              |
      | qa-email          | Brian.Findus@session.com |
      | qa-mobile         | 07777123456              |
      | qa-main-address-1 | Sea View                 |
      | qa-main-address-2 | 5 High Street            |
      | qa-main-address-3 | Nether Edge              |
      | qa-main-address-4 | Sheffield                |
      | qa-main-address-5 | Yorkshire                |
      | qa-main-address-6 | S10 1EQ                  |

  Scenario: Delius user views offender who does not have a main address or contact details registered

    Given that the offender has the following data from json file "offenderMissingData" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    And they expand the "Contact details" content section
    Then the page should display the following by class name
      | qa-telephone         | Unknown         |
      | qa-email             | Unknown         |
      | qa-mobile            | Unknown         |
      | qa-main-address-none | No main address |

  Scenario: Delius user views offender who has a main address registered as no fixed abode

    Given that the offender has the following data from json file "offenderMainAddressNFA" in Delius
    When they navigate to the offender summary page
    And they expand the "Offender details" accordion
    And they expand the "Contact details" content section
    Then the page should display the following by class name
      | qa-main-address-nfa | No fixed abode |
