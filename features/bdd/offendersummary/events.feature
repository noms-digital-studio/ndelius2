Feature: Offender Summary: Events


  Scenario: Offender has no events saved within Delius

    Given that the offender has no events saved within Delius
    And they navigate to the offender summary page
    When  the Delius user selects the "Events" link on the "Offender Summary" UI
    Then  they should see the following event text "no data"

  Scenario: Offender has three events saved within Delius
    Given that the offender has the following event information saved in Delius
      | Main Offence                                                                                | Outcome                         | Sentence | App Date   | Status     |
      | Abstracting electricity - 04300                                                             | Adjourned - Pre-Sentence Report |          | 05/11/2018 | Active     |
      | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604 | Deferred Sentence               |          | 26/11/2018 | Active     |
      | Acknowledging bail in false name - 08303                                                    | Hearing date changed            |          | 29/11/2018 | Terminated |
    And they navigate to the offender summary page
    When the Delius user selects the "Events" link on the "Offender Summary" UI
    Then they should see the following event information
      | Outcome                         | Main Offence                                                                                | App Date   | Status     |
      | Hearing date changed            | Acknowledging bail in false name - 08303                                                    | 29/11/2018 | Terminated |
      | Deferred Sentence               | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604 | 26/11/2018 | Active     |
      | Adjourned - Pre-Sentence Report | Abstracting electricity - 04300                                                             | 05/11/2018 | Active     |

  Scenario: Offender has six events saved within Delius
    Given that the offender has the following event information saved in Delius
      | Main Offence                                                                                                                               | Outcome                                  | Sentence                                 | App Date   | Status     |
      | Abstracting electricity - 04300                                                                                                            | Adjourned - Pre-Sentence Report          |                                          | 05/11/2018 | Active     |
      | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | Deferred Sentence                        |                                          | 26/10/2018 | Active     |
      | Acknowledging bail in false name - 08303                                                                                                   | Hearing date changed                     |                                          | 29/11/2018 | Terminated |
      | Abstracting electricity - 04300                                                                                                            | Adjourned - Pre-Sentence Report          |                                          | 05/11/2017 | Active     |
      | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months) | 10/05/2016 | Terminated |
      | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | ORA Suspended Sentence Order (24 Months) | ORA Suspended Sentence Order (24 Months) | 10/06/2017 | Active     |
    And they navigate to the offender summary page
    When the Delius user selects the "Events" link on the "Offender Summary" UI
    And they select "Show more events" hyperlink from the UI
    Then they should see the following event information
      | Outcome                                  | Main Offence                                                                                                                               | App Date   | Status     |
      | Hearing date changed                     | Acknowledging bail in false name - 08303                                                                                                   | 29/11/2018 | Terminated |
      | Adjourned - Pre-Sentence Report          | Abstracting electricity - 04300                                                                                                            | 05/11/2018 | Active     |
      | Deferred Sentence                        | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | 26/10/2018 | Active     |
      | Adjourned - Pre-Sentence Report          | Abstracting electricity - 04300                                                                                                            | 05/11/2017 | Active     |
      | ORA Suspended Sentence Order (24 Months) | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | 10/06/2017 | Active     |
      | ORA Suspended Sentence Order (12 Months) | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | 10/05/2016 | Terminated |

  Scenario: Offender has twenty events saved within Delius
    Given that the offender has the following event information saved in Delius
      | Main Offence                                                                                                                               | Outcome                                  | Sentence                                 | App Date   | Status     |
      | Abstracting electricity - 04300                                                                                                            | Adjourned - Pre-Sentence Report          |                                          | 05/11/2018 | Active     |
      | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | Deferred Sentence                        |                                          | 26/10/2018 | Active     |
      | Acknowledging bail in false name - 08303                                                                                                   | Hearing date changed                     |                                          | 29/11/2018 | Terminated |
      | Abstracting electricity - 04300                                                                                                            | Adjourned - Pre-Sentence Report          |                                          | 05/11/2017 | Active     |
      | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months) | 10/05/2016 | Terminated |
      | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | ORA Suspended Sentence Order (24 Months) | ORA Suspended Sentence Order (24 Months) | 10/06/2017 | Active     |
      | Abstracting electricity - 04300                                                                                                            | Adjourned - Pre-Sentence Report          |                                          | 05/10/2018 | Active     |
      | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months) | 26/11/2016 | Terminated |
      | Acknowledging bail in false name - 08303                                                                                                   | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months) | 29/11/2016 | Terminated |
      | Abstracting electricity - 04300                                                                                                            | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months  | 05/11/2015 | Terminated |
      | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months) | 10/03/2016 | Terminated |
      | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | ORA Suspended Sentence Order (24 Months) | ORA Suspended Sentence Order (24 Months) | 10/02/2017 | Active     |
      | Abstracting electricity - 04300                                                                                                            | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months) | 05/09/2015 | Terminated |
      | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months) | 26/08/2018 | Active     |
      | Acknowledging bail in false name - 08303                                                                                                   | ORA Suspended Sentence Order (18 Months) | ORA Suspended Sentence Order (18 Months) | 29/11/2014 | Terminated |
      | Abstracting electricity - 04300                                                                                                            | ORA Suspended Sentence Order (12 Months) | ORA Suspended Sentence Order (12 Months) | 05/11/2016 | Terminated |
      | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | ORA Suspended Sentence Order (18 Months) | ORA Suspended Sentence Order (18 Months) | 23/05/2016 | Terminated |
      | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | ORA Suspended Sentence Order (24 Months) | ORA Suspended Sentence Order (24 Months) | 04/02/2015 | Terminated |
      | Abstracting electricity - 04300                                                                                                            | ORA Suspended Sentence Order (36 Months) | ORA Suspended Sentence Order (36 Months) | 01/03/2014 | Terminated |
      | Abstracting electricity - 04300                                                                                                            | ORA Suspended Sentence Order (36 Months) | ORA Suspended Sentence Order (36 Months) | 01/03/2015 | Terminated |
    And they navigate to the offender summary page
    When the Delius user selects the "Events" link on the "Offender Summary" UI
    Then they should see the following event information
      | Outcome                         | Main Offence                                                                                | App Date   | Status     |
      | Hearing date changed            | Acknowledging bail in false name - 08303                                                    | 29/11/2018 | Terminated |
      | Adjourned - Pre-Sentence Report | Abstracting electricity - 04300                                                             | 05/11/2018 | Active     |
      | Deferred Sentence               | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604 | 26/10/2018 | Active     |
    And they select "Show more events" hyperlink from the UI
    Then they should see the following event information
      | Outcome                                  | Main Offence                                                                                                                               | App Date   | Status     |
      | Hearing date changed                     | Acknowledging bail in false name - 08303                                                                                                   | 29/11/2018 | Terminated |
      | Adjourned - Pre-Sentence Report          | Abstracting electricity - 04300                                                                                                            | 05/11/2018 | Active     |
      | Deferred Sentence                        | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | 26/10/2018 | Active     |
      | Adjourned - Pre-Sentence Report          | Abstracting electricity - 04300                                                                                                            | 05/10/2018 | Active     |
      | ORA Suspended Sentence Order (12 Months) | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | 26/08/2018 | Active     |
      | Adjourned - Pre-Sentence Report          | Abstracting electricity - 04300                                                                                                            | 05/11/2017 | Active     |
      | ORA Suspended Sentence Order (24 Months) | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | 10/06/2017 | Active     |
      | ORA Suspended Sentence Order (24 Months) | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | 10/02/2017 | Active     |
      | ORA Suspended Sentence Order (12 Months) | Acknowledging bail in false name - 08303                                                                                                   | 29/11/2016 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | 26/11/2016 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Abstracting electricity - 04300                                                                                                            | 05/11/2016 | Terminated |
      | ORA Suspended Sentence Order (18 Months) | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | 23/05/2016 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | 10/05/2016 | Terminated |
    And they select "Show more events" hyperlink from the UI
    Then they should see the following event information
      | Outcome                                  | Main Offence                                                                                                                               | App Date   | Status     |
      | Hearing date changed                     | Acknowledging bail in false name - 08303                                                                                                   | 29/11/2018 | Terminated |
      | Adjourned - Pre-Sentence Report          | Abstracting electricity - 04300                                                                                                            | 05/11/2018 | Active     |
      | Deferred Sentence                        | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | 26/10/2018 | Active     |
      | Adjourned - Pre-Sentence Report          | Abstracting electricity - 04300                                                                                                            | 05/10/2018 | Active     |
      | ORA Suspended Sentence Order (12 Months) | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | 26/08/2018 | Active     |
      | Adjourned - Pre-Sentence Report          | Abstracting electricity - 04300                                                                                                            | 05/11/2017 | Active     |
      | ORA Suspended Sentence Order (24 Months) | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | 10/06/2017 | Active     |
      | ORA Suspended Sentence Order (24 Months) | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | 10/02/2017 | Active     |
      | ORA Suspended Sentence Order (12 Months) | Acknowledging bail in false name - 08303                                                                                                   | 29/11/2016 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Detaining and threatening to kill or injure a hostage (Taking of Hostages Act 1982) - 03604                                                | 26/11/2016 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Abstracting electricity - 04300                                                                                                            | 05/11/2016 | Terminated |
      | ORA Suspended Sentence Order (18 Months) | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | 23/05/2016 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | 10/05/2016 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Stealing mail bags or postal packets or unlawfully taking away or opening mail bag - 04200                                                 | 10/03/2016 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Abstracting electricity - 04300                                                                                                            | 05/11/2015 | Terminated |
      | ORA Suspended Sentence Order (12 Months) | Abstracting electricity - 04300                                                                                                            | 05/09/2015 | Terminated |
      | ORA Suspended Sentence Order (36 Months) | Abstracting electricity - 04300                                                                                                            | 01/03/2015 | Terminated |
      | ORA Suspended Sentence Order (24 Months) | Town and Country Planning Act 1990/Planning (Listed Buildings and Conservation Areas) Act 1990/Planning (Hazardous Substances Act) - 09400 | 04/02/2015 | Terminated |
      | ORA Suspended Sentence Order (18 Months) | Acknowledging bail in false name - 08303                                                                                                   | 29/11/2014 | Terminated |
      | ORA Suspended Sentence Order (36 Months) | Abstracting electricity - 04300                                                                                                            | 01/03/2014 | Terminated |
