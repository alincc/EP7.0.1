#TODO: investigate way to test time fields
@Items

Feature: Items

  Background:
    Given I am logged in as a public user

  Scenario Outline: Item displays a list of details that are additional textural data for that item for sale
    Given I look up an item with code <SKU_WITH_DEFINED_ATTRIBUTES>
    When I follow links definition
    Then the field display-name has value <DISPLAY_NAME>
    And the field name does not exist
    And the SKU attributes contain
      | name   | display name    | value                                                        | display value                                                |
      | A00001 | Plot Keyword    | Robot, Orphan, 1030s, Train Station, Mystery, إنسان آلي      | Robot, Orphan, 1030s, Train Station, Mystery, إنسان آلي      |
#      | A00002 | Release Date    | 1321171200000                                                | 2011                                            |
      | A00003 | Storyline       | The "Invention of Hugo Cabret" concerns a 12-year-old orphan | The "Invention of Hugo Cabret" concerns a 12-year-old orphan |
      | A00004 | Languages       | Anglais, Français, Italien, Allemand                         | Anglais, Français, Italien, Allemand                         |
#      | A00005 | Opening DayTime | 1322269361000                                                | 2011                                                         |
      | A00006 | Color           | true                                                         | True                                                         |
      | A00007 | Runtime         | 127                                                          | 127                                                          |
      | A00009 | Viewer's Rating | 8.12                                                         | 8.12                                                         |
      | A00013 | Format          | 35 mm, 1.85 : 1                                              | 35 mm, 1.85 : 1                                              |

    Examples:
      | SKU_WITH_DEFINED_ATTRIBUTES | DISPLAY_NAME |
      | tt0970179_sku               | Hugo         |

  Scenario Outline: Item does not display attributes that have no value defined
    Given I look up an item with code <SKU_WITH_NO_VALUE_DEFINED_ATTRIBUTES>
    When I follow links definition
    Then the field details does not contain value Tagline
    And the field details does not contain value A00011

    Examples:
      | SKU_WITH_NO_VALUE_DEFINED_ATTRIBUTES |
      | tt0970179_sku                        |

  Scenario Outline: Item displays details based on scope locale
    Given I look up an item with code <SKU_WITH_SCOPE_SPECIFIC_ATTRIBUTES>
    And I follow links definition
    And the field display-name has value Hugo
    And the field details contains value The "Invention of Hugo Cabret"
    And the field details does not contain value L'invention de "Hugo Cabret"
    When I am logged into scope <FRENCH_SCOPE> as a public user
    And I look up an item with code <SKU_WITH_SCOPE_SPECIFIC_ATTRIBUTES>
    And I follow links definition
    Then the field display-name has value les aventures de Hugo
    And the field details does not contain value The "Invention of Hugo Cabret"
    And the field details contains value L'invention de "Hugo Cabret"

    Examples:
      | FRENCH_SCOPE | SKU_WITH_SCOPE_SPECIFIC_ATTRIBUTES |
      | toastie      | tt0970179_sku                      |

  Scenario Outline: Item with no attributes associated with it will not display details field
    Given I look up an item with code <SKU_WITH_NO_ATTRIBUTES>
    When I follow links definition
    Then the array field details is empty

    Examples:
      | SKU_WITH_NO_ATTRIBUTES |
      | t384lkef               |