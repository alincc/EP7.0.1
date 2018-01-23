@Geographies
Feature: Geographies - Retrieve List of Supported Countries
  As a client developer,
  I want to retrieve a list of supported countries,
  so that I can display the entire list for the shopper to select

Scenario: I can retrieve a complete list of all countries
Given there is a list of supported countries for scope mobee
When I request the list of countries
Then I get back all 39 supported countries

Scenario: I can retrieve the list of countries for a specific scope
Given mobee is an English scope
And one of the supported countries is Canada
When I request the list of countries in scope mobee
Then one of the countries is Canada

Scenario: I can retrieve the localized language list of countries for a specific scope
Given toastie is a French scope
And one of the supported countries is United States
When I request the list of countries in scope toastie
Then one of the countries is ETATS-UNIS
