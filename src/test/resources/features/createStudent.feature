Feature: Create student

  @wip
  Scenario: Create student as a teacher and verify status code 201
    Given I logged Bookit api using "blyst6@si.edu" and "barbabaslyst"
    When I send POST request to "/api/students/student" endpoint with following information

      | first-name      | John                 |
      | last-name       | Whatson              |
      | email           | whatson1@example.com |
      | password        | John123              |
      | role            | student-team-leader  |
      | campus-location | VA                   |
      | batch-number    | 7                    |
      | team-name       | Nukes                |

    Then status code should be 201
    And I delete previously added student

      