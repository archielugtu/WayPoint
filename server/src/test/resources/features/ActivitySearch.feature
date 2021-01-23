Feature: Finding an activity by name

  Background:
    Given I initialise the repositories
    And I have a user named "Seymour"
    And I have an activity named "Steamed Hams" created by the user named "Seymour"
    And I have an activity named "Steamed Ham" created by the user named "Seymour"

  @U35-AC1-SearchingActivity
  Scenario: User searches for an activity using key words
    When a GET request is sent by a user named "Seymour" to retrieve an activity with the keyword "Steamed Hams"
    Then the response code 200 is received
    And the activity named "Steamed Hams" is received

  @U35-AC2-SearchingActivity
  Scenario: User Searches for an activity using partial key words.
    When a GET request is sent by a user named "Seymour" to retrieve an activity with the keyword "ams"
    Then the response code 200 is received
    And the activity named "Steamed Hams" is received

  @U35-AC3-SearchingActivity
  Scenario: User Searches for an activity using key words in quotation marks.
    When a GET request is sent by a user named "Seymour" to retrieve an activity with the keyword '"Steamed Ham"'
    Then the response code 200 is received
    And the activity named "Steamed Hams" is not received
    And the activity named "Steamed Ham" is received

  @U34-AC3-SearchingActivity
  Scenario: User Searches for an activities within the boundaries 0, 10, 0, 10
    Given the activity named "Steamed Ham" has the coordinates 5, 5
    And the activity named "Steamed Hams" has the coordinates 15, 15
    When a GET request is sent to retrieve activities within the boundaries 0, 10, 0, 10
    Then the response code 200 is received
    And the activity named "Steamed Hams" is not received
    And the activity named "Steamed Ham" is received
