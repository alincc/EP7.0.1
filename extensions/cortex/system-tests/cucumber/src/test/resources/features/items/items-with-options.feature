#TODO: investigate way to test time fields
@Items

Feature: Items with options

  Background:
    Given I am logged in as a public user

  Scenario Outline: Configurable item has option link
    Given I look up an item with code <CONFIGURABLE_ITEM>
    When I follow links definition
    Then there is a options link

    Examples:
      | CONFIGURABLE_ITEM |
      | tt64464fn_hd      |

  Scenario Outline: Nonconfigurable item has no option link
    Given I look up an item with code <NON_CONFIGURABLE_ITEM>
    When I follow links definition
    Then there are no options links

    Examples:
      | NON_CONFIGURABLE_ITEM |
      | tt0970179_sku         |

  Scenario Outline: No choice links for item with only one option
#    Given the current configuration is Buy,SD; there will be no available choice for Purchase Type since Rent is not available in SD
    Given I look up an item with code <SKU_WITH_NARROWING_OPTION_CHOICE>
    When I follow links definition -> options
    And open the element with field name of PurchaseType
    And I follow links selector
    Then there are no choice links
    And there is a chosen link

    Examples:
      | SKU_WITH_NARROWING_OPTION_CHOICE |
      | tt258022dh_SD_B                  |

  Scenario Outline: Item details has generic and specific item attributes
    Given I look up an item with code <PURCHASEABLE_CONFIGURABLE_ITEM>
    When I follow links definition
    Then the sku attribute array field details contains
      | name   | value                        |
      | A00014 | High                         |
#      | A00017 | 1346346875000                |
      | A00001 | [nemo, crownfish, animation] |
#      | A00002 | 1323129067000                |
      | A00004 | [English, French]            |
      | A00007 | 120                          |
      | A00009 | 8.6                          |

    Examples:
      | PURCHASEABLE_CONFIGURABLE_ITEM |
      | tt64464fn_hd                   |

  Scenario Outline: Item has correct list of options
    Given I look up an item with code <CONFIGURABLE_ITEM>
    When I follow links definition -> options -> element
    Then the field name has value VideoQuality
    And the field display-name has value Video Quality
    And I follow links value
    And the field display-name has value High Definition
    And I follow links option -> selector -> choice -> description
    And the field display-name has value Standard Definition

    Examples:
      | CONFIGURABLE_ITEM |
      | tt64464fn_hd      |