@OffenderSummary
Feature: Offender summary - identity details

  Scenario: Delius user views offender who is in NOMIS

    Given that the offender has the following offender details in Delius
      | firstName  | John |
      | surname  | Smith |
      | dateOfBirth  | 1998-06-22 |
      | otherIds.crn  | X123456 |
      | otherIds.nomsNumber  | A12345K |
    When they navigate to the offender summary page
    Then they see the offender name as "Smith, John"
    And they see the offender CRN as "X123456"
    And they see the offender date of birth as "22/06/1998"
    And they see the offender mugshot

  Scenario: Delius user views offender who is not in NOMIS

    Given that the offender has the following offender details in Delius
      | firstName  | John |
      | surname  | Smith |
      | dateOfBirth  | 1998-06-22 |
      | otherIds.crn  | X123456 |
      | otherIds.nomsNumber  |  |
    When they navigate to the offender summary page
    Then they see the offender name as "Smith, John"
    And they see the offender CRN as "X123456"
    And they see the offender date of birth as "22/06/1998"
    And they see the offender placeholder mugshot

