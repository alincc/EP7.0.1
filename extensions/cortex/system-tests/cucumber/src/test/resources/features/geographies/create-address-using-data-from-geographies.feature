@Geographies
Feature: Geographies - Retrieve Specific Country and Region Codes
  As a client developer,
  I want to retrieve the codes for a specific country and region,
  so that I can create an address

  Scenario: Retrieve specific country and region codes to create an address
    Given there is a list of supported countries and regions for scope mobee
    When the country Canada and the region British Columbia is selected
    Then I can obtain the country and region code to create an address