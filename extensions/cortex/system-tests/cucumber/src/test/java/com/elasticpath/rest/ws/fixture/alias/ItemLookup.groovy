package com.elasticpath.rest.ws.fixture.alias

class ItemLookup {

	def client

	def ItemLookup(client) {
		this.client = client
		client.alias(this.&findItemByDisplayName)
		client.alias(this.&search)
		client.alias(this.&navigate)
		client.alias(this.&lookups)
	}

	def search(keyword) {
		client.GET("/")
				.searches()
				.keywordsearchform()\
				.itemkeywordsearchaction(
						['keywords': keyword]
				)
				.follow()
				.findItemByDisplayName(keyword)
	}

	def navigate(categoryName, itemName){
		client.GET("/")
				.navigations()
				.findElement {
					category ->
						category["name"] == categoryName
				}
				.items()
				.findItemByDisplayName(itemName)
	}

	def findItemByDisplayName(displayName) {
		client.findElement {
			item ->
				def definition = item.definition()
				definition["display-name"] == displayName
		}
	}

	def lookups(scope) {
		client.GET("/lookups/$scope")
	}
}
