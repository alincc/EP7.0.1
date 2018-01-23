@Orders
Feature: Test billing address on order

  @HeaderAuth
  Scenario: Default is set on order when I have existing default billing address on profile
    Given I login as a registered user
    And Shopper gets the default billing address
    When I retrieve the shoppers billing address info on the order
    Then the default billing address is automatically applied to the order

  Scenario: Default is set on order when I create default billing address on profile
    Given I have authenticated as a newly registered shopper
    And the shoppers order does not have a billing address applied
    When I create a default billing address on the profile
    And I retrieve the shoppers billing address info on the order
    Then the default billing address is automatically applied to the order

  Scenario: Cart with no products has needinfo link on order when no billing address set
    Given I am logged in as a public user
    When I retrieve the order
    Then there is a needinfo link to billing-address-info

  Scenario: Cart with product has needinfo link on order when no billing address set
    Given I am logged in as a public user
    When I add item Twilight to the cart
    Then I retrieve the order
    And there is a needinfo link to billing-address-info

  Scenario:  Needinfo link on order is removed when billing address set
    Given I am logged in as a public user
    When I add item Twilight to the cart
    And billing address is selected
    When I retrieve the order
    Then there is no needinfo link to billing-address-info

  Scenario:  Billing address choice becomes chosen when selected
    Given I have authenticated as a newly registered shopper
    And I add an address with country CA and region BC
    And I add an address with country CA and region QC
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    And there is a choice link
    And there is a chosen link
    When I follow links choice
    And save the choice uri
    And I use the selectaction
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    Then the saved choice uri has rel chosen

  Scenario:  Billing address chosen becomes choice when new address is selected
    Given I have authenticated as a newly registered shopper
    And I add an address with country CA and region BC
    And I add an address with country CA and region QC
    And I retrieve the order
    And I follow links billingaddressinfo -> selector -> chosen
    And save the chosen uri
    And I retrieve the order
    And I follow links billingaddressinfo -> selector -> choice
    And I use the selectaction
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    Then the saved chosen uri has rel choice

  Scenario:  Billing address chosen can be reselected
    Given I have authenticated as a newly registered shopper
    And I add an address with country CA and region BC
    And I add an address with country CA and region QC
    And I retrieve the order
    And I follow links billingaddressinfo -> selector -> chosen
    And save the chosen uri
    And I use the selectaction
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    Then the saved chosen uri has rel chosen