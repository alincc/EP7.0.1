@Geographies

Feature: Geographies - Retrieve List of Regions For a Given Supported Country
  As a client developer,
  I want to retrieve a list of regions available for a country,
  so I can display the list for the shopper to select

Scenario: I can retrieve the complete list of all regions for Canada
Given there is a list of supported countries and regions for scope mobee
When I request the list of regions for Canada in scope mobee
Then I get back all 13 supported regions for Canada

Scenario: I get back an empty list of regions for a country where no regions have been configured
Given there are no supported regions for Japan
When I request the list of regions for Japan
Then I get back an empty list

Scenario: List of regions for Canada is in English when using an English scope
Given mobee is an english scope
And one of the supported regions for Canada is British Columbia
When I request the list of regions for Canada in scope mobee
Then one of the regions is British Columbia

Scenario: List of regions for Canada is in French when using a French scope
Given toastie is a french scope
And one of the supported regions for Canada is British Columbia
When I request the list of regions for Canada in scope toastie
Then one of the regions is Colombie Britannique
