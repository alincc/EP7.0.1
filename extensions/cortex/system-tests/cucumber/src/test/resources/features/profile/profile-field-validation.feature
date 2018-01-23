@Profile
Feature: Profile field validations

  Scenario Outline: Profile required field validation - multi fields
    When I have authenticated as a newly registered shopper
    And I update my profile family-name <FAMILY_NAME> and given-name <GIVEN_NAME>
    Then the HTTP status is bad request
    And I should see validation error message with error type, debug message, and field
      | errorID        | debugMessage                      | fieldName   |
      | field.required | family-name attribute is required | family-name |
      | field.required | given-name attribute is required  | given-name  |

    Examples:
      | FAMILY_NAME | GIVEN_NAME |
      |             |            |

  Scenario Outline: Profile required field validation - single field
    When I have authenticated as a newly registered shopper
    And I update my profile family-name <FAMILY_NAME> and given-name <GIVEN_NAME>
    Then the HTTP status is bad request
    And I should see validation error message with error type, debug message, and field
      | errorID        | debugMessage                     | fieldName  |
      | field.required | given-name attribute is required | given-name |

    Examples:
      | FAMILY_NAME | GIVEN_NAME |
      | testUpdate  |            |

#	Anonymous user email is still using the old email validation. the HTTP status is 400 with only string message.
  Scenario Outline: Profile Email validation - anonymous user
    When I am logged in as a public user
    And I create invalid email <EMAIL>
    Then the HTTP status is bad request

    Examples:
      | EMAIL        |
      |              |
      | invalidEmail |

  Scenario Outline: Profile Email validation - empty fields - registered user
    When I have authenticated as a newly registered shopper
    And I create invalid email <EMAIL>
    Then the HTTP status is bad request
    And I should see validation error message with error type, debug message, and field
      | errorID        | debugMessage                | fieldName |
      | field.required | email attribute is required | email     |
      | field.required | username may not be null    | username  |

    Examples:
      | EMAIL |
      |       |

  Scenario Outline: Profile Email validation - invalid email - registered user
    When I have authenticated as a newly registered shopper
    And I create invalid email <EMAIL>
    Then the HTTP status is bad request
    And I should see validation error message with error type, debug message, and field
      | errorID                    | debugMessage                    | fieldName |
      | field.invalid.email.format | not a well-formed email address | email     |

    Examples:
      | EMAIL        |
      | invalidEmail |

  Scenario Outline: Profile Email validation - non-unique email - registered user
    When I have authenticated as a newly registered shopper
    And I create invalid email <EMAIL>
    Then the HTTP status is conflict
    And I should see validation error message with error type and debug message
      | errorID                       | debugMessage                                   |
      | profile.userid.already.exists | Customer with the given user Id already exists |

    Examples:
      | EMAIL                        |
      | harry.potter@elasticpath.com |