@smoketest @signin @invalidSignin
Feature: Invalid signin

	Scenario Outline: Invalid sign in to CM
		When I sign in to CM as <INVALID_ID> with password <INVALID_PASSWORD>
		Then I should not be able to sign in

		Examples:
			| INVALID_ID | INVALID_PASSWORD |
			| abc        | 111111           |
