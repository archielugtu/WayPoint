Feature: Creating outcomes for activities

  Background:
    Given I initialise the repositories
    And I have a user named "SuperNintendoChalmers"
    And I have a user named "Seymour"
    And I have an activity named "Steamed Hams" created by the user named "Seymour"
    And the user named "SuperNintendoChalmers" has the role "participant" for the activity named "Steamed Hams"
    And the activity named "Steamed Hams" has the outcomes
      | You call hamburgers steamed hams? | Checkbox |


  @U33-AC1-CreatingOutcomes
  Scenario: Editing an activity through a put request changes the outcome options
    When a PUT request edits the activity named "Steamed Hams" to have the outcomes
    # | Question | InputType
      | You call hamburgers steamed hams? | Checkbox |
      | Uh-huh. Uh, what region? | Text |
      | Yes. And you call them steamed hams despite the fact that they are obviously grilled | Text |
    Then the response code 200 is received
    And the activity named "Steamed Hams" has 3 outcome options


  @U33-AC1-CreatingOutcomes
  Scenario: Creating an activity through a post request has outcomes
    When a POST request adds an activity named "The house is on fire" with the outcomes
    # | Question | InputType
      | Just the northern lights? | Checkbox |
    Then the activity named "The house is on fire" has 1 outcome options

  @U33-AC1-CreatingOutcomes
  Scenario: Activities can be created without outcomes
    When a PUT request edits the activity named "Steamed Hams" to have no outcomes

    Then the activity named "Steamed Hams" has 0 outcome options



