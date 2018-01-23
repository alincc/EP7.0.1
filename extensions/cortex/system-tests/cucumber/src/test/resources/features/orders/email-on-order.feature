@Orders
Feature: Email address on order
  As a shopper,
  I want to be able to add an email address to my order on the fly,
  so that I can complete my purchase

  Background:
    Given I am logged in as a public user

  Scenario: Create an email for my order
    When I create an email for my order
    Then the email is created and selected for my order

  Scenario: I must select email address for my order to complete a purchase
    When I am not be able to submit my order
    And I am able to determine the reason is because of missing email information
