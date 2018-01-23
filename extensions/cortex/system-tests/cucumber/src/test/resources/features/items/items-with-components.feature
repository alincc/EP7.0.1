#TODO: investigate way to test time fields
@Items

Feature: Items with components

  Background:
    Given I am logged in as a public user

  Scenario Outline: Link to components allow you to navigate to the contents of the nested bundle
    Given I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    When I follow links definition -> components
    Then there is an element with field display-name of New Movies
    And there is an element with field display-name of Good Movies

    Examples:
      | PURCHASABLE_NESTED_BUNDLE |
      | mb_2893033                |

  Scenario Outline: Nested bundle correctly shows their components and quantities
    Given I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    When I follow links definition -> components
    And open the element with field display-name of <NESTED_BUNDLE>
    And I follow links components
    And save the components uri
    Then open the element with field display-name of The Social Network
    And the field quantity matches 1
    And return to the saved components uri
    And open the element with field display-name of Harry Potter and the Deathly Hallows Part 1
    And the field quantity matches 1
    And return to the saved components uri
    And open the element with field display-name of Superheroes
    And the field quantity matches 1

    Examples:
      | PURCHASABLE_NESTED_BUNDLE | NESTED_BUNDLE |
      | mb_2893033                | New Movies    |

  Scenario Outline: Can view details of component attributes
    Given I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    When I follow links definition -> components
    And open the element with field display-name of <NESTED_BUNDLE>
    And I follow links components
    And open the element with field display-name of <NESTED_BUNDLE_COMPONENT>
    Then the sku attribute array field details contains
      | name   | value            |
      | A00001 | []               |
#      | A00002 | 1325713752000    |
      | A00004 | [Hindi, Russian] |
      | A00007 | 120              |
      | A00009 | 5                |

    Examples:
      | PURCHASABLE_NESTED_BUNDLE | NESTED_BUNDLE | NESTED_BUNDLE_COMPONENT |
      | mb_2893033                | New Movies    | The Social Network      |

  Scenario Outline: A component that is not sold separately is restricted from being purchased from outside the bundle
    Given I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    And I follow links definition -> components
    And open the element with field display-name of <NESTED_BUNDLE>
    And I follow links components
    And open the element with field display-name of <NESTED_BUNDLE_COMPONENT>
    When I follow links standaloneitem -> definition
    And the field display-name has value <NESTED_BUNDLE_COMPONENT>
    And I follow links item -> addtocartform
    Then there are no addtodefaultcartaction links
    And post to a created addtodefaultcartaction uri
    And the HTTP status is forbidden

    Examples:
      | PURCHASABLE_NESTED_BUNDLE | NESTED_BUNDLE | NESTED_BUNDLE_COMPONENT |
      | mb_2893033                | New Movies    | The Social Network      |

  Scenario Outline: Bundle component has option values and links to correct standalone variant
    Given I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    And I follow links definition -> components
    And open the element with field display-name of <NESTED_BUNDLE>
    And I follow links components
    And open the element with field display-name of <NESTED_BUNDLE_COMPONENT_WITH_OPTIONS>
    When I follow links options -> element
    And there are no selector links
    And the field display-name has value <COMPONENT_OPTION_NAME>
    And I follow links value
    Then the field display-name has value <COMPONENT_OPTION_VALUE>
    And I follow links option -> list -> definition -> standaloneitem
    And I follow links definition -> options -> element -> value
    And the field display-name has value <COMPONENT_OPTION_VALUE>

    Examples:
      | PURCHASABLE_NESTED_BUNDLE | NESTED_BUNDLE | NESTED_BUNDLE_COMPONENT_WITH_OPTIONS | COMPONENT_OPTION_NAME | COMPONENT_OPTION_VALUE |
      | mb_2893033                | New Movies    | Superheroes                          | Resolution            | 400 Pixels             |

  Scenario Outline: Bundle components that are non-configurable do not have option link
    Given I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    And I follow links definition -> components
    And open the element with field display-name of <NESTED_BUNDLE>
    And I follow links components
    When open the element with field display-name of <NESTED_BUNDLE_COMPONENT_WITH_NO_OPTIONS>
    Then there are no options links

    Examples:
      | PURCHASABLE_NESTED_BUNDLE | NESTED_BUNDLE | NESTED_BUNDLE_COMPONENT_WITH_NO_OPTIONS |
      | mb_2893033                | New Movies    | The Social Network                      |


