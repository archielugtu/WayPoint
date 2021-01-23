Feature: Subscribing to activities

  Background:
    Given I initialise the repositories
    And I have a user named "SuperNintendoChalmers"
    And I have a user named "Seymour"
    And I have an activity named "steamed hams" created by the user named "Seymour"
    And the user named "Seymour" has the role "creator" for the activity named "steamed hams"
    And the user named "SuperNintendoChalmers" has the role "follower" for the activity named "steamed hams"




  @U16-AC1-FollowActivity
  Scenario: User can follow an activity
    When a follow POST request for the activity "steamed hams" is sent from the user named "SuperNintendoChalmers"
    Then the activity named "steamed hams" has 1 follower(s)

  @U16-AC2-UnFollowActivity
  Scenario: User can unfollow an activity
    Given the activity named "steamed hams" has the user named "SuperNintendoChalmers" as a follower
    When an unfollow DELETE request for the activity "steamed hams" is sent from the user named "SuperNintendoChalmers"
    Then the activity named "steamed hams" has 0 follower(s)

  @U16-AC3-HomeFeedPostOnFollow
  Scenario: Following an activity should create a post on the home feed
    When a follow POST request for the activity "steamed hams" is sent from the user named "SuperNintendoChalmers"
    Then the user named "SuperNintendoChalmers" will have 1 update on their feed

  @U16-AC3-HomeFeedPostOnUnFollow
  Scenario: Un-following an activity should create a post on the home feed
    Given the activity named "steamed hams" has the user named "SuperNintendoChalmers" as a follower
    When an unfollow DELETE request for the activity "steamed hams" is sent from the user named "SuperNintendoChalmers"
    Then the user named "SuperNintendoChalmers" will have 1 update on their feed

  @U16-AC5-HomeFeedPostOnParticipation
  Scenario: Adding a participation should create a post on the home feed
    Given the activity named "steamed hams" has the user named "SuperNintendoChalmers" as a follower
    When a PUT request edits the activity named "steamed hams" to have the name "unforgettable luncheon" form the user named "Seymour"
    Then the user named "SuperNintendoChalmers" will have 1 update on their feed
