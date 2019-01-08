Feature: Offender Summary - notes


  Scenario: Offender has one note saved in Delius

    Given that the offender has the following notes saved within Delius
      | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod |
    When they navigate to the offender summary page
    Then they should see the following note
      | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod |


  Scenario: Offender has no notes saved in Delius

    Given that the offender has no note saved within Delius
    When they navigate to the offender summary page
    Then  they should see an empty field for the note


  Scenario: Offender has multiple notes saved in Delius

    Given that the offender has the following notes saved within Delius
      | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod                 |
      | -----------------------                                                                 |
      | Neque viverra justo nec ultrices dui sapien eget                                        |
      | -----------------------                                                                 |
      | Tortor aliquam nulla facilisi cras fermentum odio eu feugiat                            |
      | -----------------------                                                                 |
      | Dolor sit amet consectetur adipiscing elit duis tristique. Et tortor consequat id porta |
    When they navigate to the offender summary page
    Then they should see the following note
      | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod                 |
      | -----------------------                                                                 |
      | Neque viverra justo nec ultrices dui sapien eget                                        |
      | -----------------------                                                                 |
      | Tortor aliquam nulla facilisi cras fermentum odio eu feugiat                            |
      | -----------------------                                                                 |
      | Dolor sit amet consectetur adipiscing elit duis tristique. Et tortor consequat id porta |
