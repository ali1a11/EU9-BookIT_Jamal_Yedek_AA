@smoke
Feature: User Verification


  Scenario: verify information about logged user
    Given I logged Bookit api using "wcanadinea@ihg.com" and "waverleycanadine"
    When I get the current user information from api
    Then status code should be 200

  @db
  Scenario: verify information about logged user from api and database
    Given I logged Bookit api using "blyst6@si.edu" and "barbabaslyst"
    When I get the current user information from api
    Then the information about current user from api and database should match

  @db
  Scenario: three point verification (UI, API, Database)
    Given user logs in using "wcanadinea@ihg.com" "waverleycanadine"
    And user is on the my self page
    Given I logged Bookit api using "wcanadinea@ihg.com" and "waverleycanadine"
    When I get the current user information from api
    Then UI, API and Database user information must be match

  @db
  Scenario Outline: three point verification (UI, API, Database) DDT
    Given user logs in using "<email>" "<password>"
    And user is on the my self page
    Given I logged Bookit api using "<email>" and "<password>"
    When I get the current user information from api
    Then UI, API and Database user information must be match

    Examples:
      | email              | password         |
      | wcanadinea@ihg.com | waverleycanadine |
      | blyst6@si.edu      | barbabaslyst     |

  @wip @db
  Scenario Outline: three point verification (UI, API, Database) DDT Homework
    Given user logs in using "<email>" "<password>"
    And user is on the my self page
    Given I logged Bookit api using "<email>" and "<password>"
    When I get the current user information set from api
    Then UI, API and Database user information set must be match
    Examples:
      | email              | password         |
      | raymond@cydeo.com  | abs123           |
      | wcanadinea@ihg.com | waverleycanadine |



