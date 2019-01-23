Feature: Offender Summary - offender manager

  Scenario: Offender has not been allocated a Offender Manager

    Given that the offender has the following offender manager in Delius

      | Current Provider | Current Team           | Current Offender Manager      | Cluster       | LDU          | Team Telephone number | Reason for allocation           | Date allocated |
      | NPS London       | Unallocated Team (No7) | Staff, Unallocated Staff(N07) | N07 Cluster 1 | N07 Division |                       | Reallocation -Inactive offender | 05/11/2018     |

    And they navigate to the offender summary page
    And they expand the "Offender manager" accordion
    Then they should see the following offender manager details

      | Provider   | Cluster       | LDU and team                          | Officer     | Team telephone | Date allocated | Reason for allocation           |
      | NPS London | N07 Cluster 1 | N07 Division & Unallocated Team (No7) | Unallocated | Unknown        | 05/11/2018     | Reallocation -Inactive offender |


  Scenario: Offender has an offender manager

    Given that the offender has the following offender manager in Delius

      | Current Provider | Current Team | Current Offender Manager | Cluster       | LDU        | Team Telephone number | Reason for allocation | Date allocated |
      | CPA Northumbria  | OMU A        | Anyld, Annette ZZ        | C01 Cluster 1 | C01 County | 029876 1234           | Offender Moved        | 12/10/2016     |

    And they navigate to the offender summary page
    And they expand the "Offender manager" accordion
    Then they should see the following offender manager details

      | Provider        | Cluster       | LDU and team       | Officer           | Team telephone | Date allocated | Reason for allocation |
      | CPA Northumbria | C01 Cluster 1 | C01 County & OMU A | Anyld, Annette ZZ | 029876 1234    | 12/10/2016     | Offender Moved        |
