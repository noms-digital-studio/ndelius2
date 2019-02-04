@OffenderSummary
Feature: Offender Summary - Offender manager and Events cards

  Scenario: Offender has no active events

    Given that the offender has no events saved within Delius
    When they navigate to the offender summary page
    Then they should see the number of events as "0 events"
    And they should see offender status as "Not current"
    And they should not see active event data
    And they should not see provider data
    And they should not see offender manager data


  Scenario: Offender has multiple active events and has been assigned an offender manager

    Given that the offender has the following event information saved in Delius
      | Main Offence                                                                                                                               | Outcome                                  | Sentence                                 | App Date   | Status     |
      | Abstracting electricity - 04300                                                                                                            | Adjourned - Pre-Sentence Report          |                                          | 05/06/2018 | Active     |
      | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | ORA Suspended Sentence Order (24 Months) | ORA Suspended Sentence Order (24 Months) | 10/11/2018 | Active     |
      | Abstracting electricity - 04300                                                                                                            | Adjourned - Pre-Sentence Report          |                                          | 05/10/2018 | Active     |
      | Acknowledging bail in false name - 08303                                                                                                   | Hearing date changed                     |                                          | 29/11/2018 | Terminated |

    And that the offender has the following offender manager in Delius
      | Current Provider | Current Team | Current Offender Manager | Cluster       | LDU        | Team Telephone number | Reason for allocation | Date allocated |
      | CPA Northumbria  | OMU A        | Anyld, Annette ZZ        | C01 Cluster 1 | C01 County | 029876 1234           | Offender Moved        | 12/10/2016     |

    When they navigate to the offender summary page
    Then they should see the number of events as "4 events (3 active)"
    And they should see the active events data as "Last active event: ORA Suspended Sentence Order (24 Months)"
    And they should see offender status as "Current offender"
    And they should see provider data as "Provider: CPA Northumbria"
    And they should see offender manager data as "Offender manager: Anyld, Annette ZZ"


  Scenario: Offender has one active event that has not yet been sentenced and has not been assigned an offender manager

    Given that the offender has the following event information saved in Delius
      | Main Offence                    | Outcome                         | Sentence | App Date   | Status |
      | Abstracting electricity - 04300 | Adjourned - Pre-Sentence Report |          | 05/06/2018 | Active |

    And that the offender has the following offender manager in Delius
      | Current Provider | Current Team           | Current Offender Manager      | Cluster       | LDU          | Team Telephone number | Reason for allocation           | Date allocated |
      | NPS London       | Unallocated Team (No7) | Staff, Unallocated Staff(N07) | N07 Cluster 1 | N07 Division |                       | Reallocation -Inactive offender | 05/11/2018     |

    When they navigate to the offender summary page
    Then they should see the number of events as "1 event (1 active)"
    And they should see the active events data as "Last active event: Adjourned - Pre-Sentence Report"
    And they should see offender status as "Current offender"
    And they should see provider data as "Provider: NPS London"
    And they should see offender manager data as "Offender manager: Unallocated"


  Scenario: Offender has one event but is inactive and has old offender manager data

    Given that the offender has the following event information saved in Delius
      | Main Offence                    | Outcome                         | Sentence | App Date   | Status     |
      | Abstracting electricity - 04300 | Adjourned - Pre-Sentence Report |          | 05/11/2018 | Terminated |

    And that the offender has the following offender manager in Delius
      | Current Provider | Current Team           | Current Offender Manager      | Cluster       | LDU          | Team Telephone number | Reason for allocation           | Date allocated |
      | NPS London       | Unallocated Team (No7) | Staff, Unallocated Staff(N07) | N07 Cluster 1 | N07 Division |                       | Reallocation -Inactive offender | 05/11/2018     |

    When they navigate to the offender summary page
    Then they should see the number of events as "1 event (0 active)"
    And they should see offender status as "Not current"
    And they should not see active event data
    And they should not see provider data
    And they should not see offender manager data
