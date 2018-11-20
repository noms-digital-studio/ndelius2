Feature: Parole Report Landing Page - Continue

  Scenario: Delius User wants to continue writing a Parole Report for a Prisoner

    Given that the user is on the Parole Report landing page for an existing report
    When  they select the "Continue now" button on the landing page
    Then  the user should be directed to the last page that they were on working on

  Scenario Outline: A time stamp must be displayed on the landing page to state when the user last worked on the report

    Given I had previously edited a report "<minutes>" minutes ago
    When when I navigate to the Parole Report landing page for that report
    Then I should the the timestamp "<timestamp>" display indicating when the report was last edited

    Examples:
      | minutes | timestamp |
      | 1       | 1 minute ago |
      | 10      | 10 minutes ago |
      | 60      | 1 hour ago |
      | 1440    | 1 day ago |
      | 144000  | 100 days ago |

  Scenario: Delius user wants to resume writing the Parole Report

    Given the prisoner named "Bill Dobson" has a valid NOMS number in NOMIS where he is known as "William Dobson"
    When the user is on the Parole Report landing page for an existing report
    Then the user must see an image of the prisoner
    And  the user must see the prisoner name "William Dobson"
    And  the user must see the "Continue now" button

