Feature: Offender Summary - personal circumstances

  Scenario: Offender has no personal circumstance saved in Delius
    Given that the offender has no personal circumstance saved in Delius
    And they navigate to the offender summary page
    And they expand the "Offender manager" accordion
    And they expand the "Personal circumstances" content section
    Then the screen should expand to show the following text "No personal circumstance recorded"

  Scenario: Offender is pregnant
    Given that the following personal circumstance information is saved for an offender within Delius
      | Circumstance type   | Circumstance subtype | Start Date | End date |
      | Pregnancy/Maternity | Pregnancy            | 17/10/2018 |          |
    And they navigate to the offender summary page
    And they expand the "Offender manager" accordion
    And they expand the "Personal circumstances" content section
    Then the following personal circumstance information must be displayed
      | Type                | Subtype   | Date       |
      | Pregnancy/Maternity | Pregnancy | 17/10/2018 |


  Scenario: Offender has muliple personal circumstance saved for Accommodation
    Given that the following personal circumstance information is saved for an offender within Delius
      | Circumstance type | Circumstance subtype      | Start Date | End date |
      | Accommodation     | Approved Premises         | 10/07/2018 |          |
      | Accommodation     | Friends/Family (settled)  | 24/04/2018 |          |
      | Accommodation     | Homeless - Rough Sleeping | 03/01/2018 |          |

    And they navigate to the offender summary page
    And they expand the "Offender manager" accordion
    And they expand the "Personal circumstances" content section
    Then the following personal circumstance information must be displayed
      | Type          | Subtype                   | Date       |
      | Accommodation | Approved Premises         | 10/07/2018 |
      | Accommodation | Friends/Family (settled)  | 24/04/2018 |
      | Accommodation | Homeless - Rough Sleeping | 03/01/2018 |


  Scenario: Offender has multiple personal circumstance saved in Delius
    Given that the following personal circumstance information is saved for an offender within Delius
      | Circumstance type            | Circumstance subtype                     | Start Date | End date   |
      | Troubled families            | Declined to participate in the TF scheme | 22/10/2018 |            |
      | Pregnancy/Maternity          | Pregnancy                                | 18/10/2018 |            |
      | General Health               | Maternity                                | 17/10/2018 |            |
      | Accommodation                | Approved Premises                        | 10/07/2018 |            |
      | Care leaver                  | Care Experienced                         | 05/05/2018 |            |
      | General Health               | Mental health concerns                   | 21/04/2018 |            |
      | Offender Level Recording (L) | Offender File Checked Out                | 18/04/2018 |            |
      | Employment                   | Apprenticeship                           | 30/03/2018 | 01/07/2020 |
      | Benefit                      | Universal Credit                         | 20/02/2018 |            |

    And they navigate to the offender summary page
    And they expand the "Offender manager" accordion
    And they expand the "Personal circumstances" content section
    Then the following personal circumstance information must be displayed
      | Type                         | Subtype                                  | Date       |
      | Troubled families            | Declined to participate in the TF scheme | 22/10/2018 |
      | Pregnancy/Maternity          | Pregnancy                                | 18/10/2018 |
      | General Health               | Maternity                                | 17/10/2018 |
      | Accommodation                | Approved Premises                        | 10/07/2018 |
      | Care leaver                  | Care Experienced                         | 05/05/2018 |
      | General Health               | Mental health concerns                   | 21/04/2018 |
      | Offender Level Recording (L) | Offender File Checked Out                | 18/04/2018 |
      | Benefit                      | Universal Credit                         | 20/02/2018 |
