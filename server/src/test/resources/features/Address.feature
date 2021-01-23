Feature: Specifying locationPlaceId for user

  Background:
    Given I initialise the repositories
    And I have a user named "Seymour"
    And I have a user named "Captain Ben"

  @U38-AC3-EditingLocation
  Scenario: User edits their locationPlaceId to a real locationPlaceId (Captain Bens).
    When a PUT request edits the user named "Captain Ben" to have locationPlaceId "ChIJ0zApXMGKMW0RRGJ5bnB1cTk"
    Then the response code 200 is received for the user
    And the user named "Captain Ben" will have the locationPlaceId "ChIJ0zApXMGKMW0RRGJ5bnB1cTk"