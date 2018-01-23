@CustomerSegments @DynamicContent
Feature:Dynamic Content can be displayed based on customer segments
    As a RelOS client
    I want to offer personalized shopping experiences for customers in my customer segment
    so these segments of customer can have the shopping experiences they desire

  Scenario Outline: Dynamic Content is displayed when customer segment condition is matched
    Given I login to the registered user with email ID <CUSTOMER_SEGMENT_ID>
    When I view the customer segment dynamic content <DC_NAME>
    Then the customer segment dynamic content is displayed

    Examples:
      | CUSTOMER_SEGMENT_ID           | DC_NAME                  |
      | itestsegment@elasticpath.com  | End Of Year Sales Banner |

  @HeaderAuth
  Scenario: Dynamic Content is not displayed when customer segment condition does not match
    Given I login as a registered user
    When I view the customer segment dynamic content End Of Year Sales Banner
    Then the customer segment dynamic content is not displayed

  @HeaderAuth
  Scenario Outline: Dynamic Content is displayed when header override includes customer segment trait
    Given I login as a registered user
    And I submit request header with the user traits <TRAITS_VALUE>
    When I view the customer segment dynamic content <DC_NAME>
    Then the customer segment dynamic content is displayed

    Examples:
     | TRAITS_VALUE                   | DC_NAME                  |
     | CUSTOMER_SEGMENT=ITEST_SEGMENT | End Of Year Sales Banner |