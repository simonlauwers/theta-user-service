Feature: user can register
  Scenario: client makes POST request to endpoint register with new email
    Given I have the following users in my database
      | email           | password      | displayName
      | simon@dev.com   | badeendjes123 | simonlauw
      | nathan@dev.com  | kropkesla92   | nathantetro
      | quinten@dev.com | spiegelei01   | quintenvh
    When the client calls endpoint register with email youssef@dev.com,
    Then the client receives status code of 200
      And the client receives the created user object

  Scenario: existing client makes POST request to endpoint register with existing email
    Given I have the following users in my database
      | email           | password      | displayName
      | simon@dev.com   | badeendjes123 | simonlauw
      | nathan@dev.com  | kropkesla92   | nathantetro
      | quinten@dev.com | spiegelei01   | quintenvh
    When the client calls endpoint register with email nathan@dev.com
    Then the client receives status code of 409
    And the client receives message that the user already exists


