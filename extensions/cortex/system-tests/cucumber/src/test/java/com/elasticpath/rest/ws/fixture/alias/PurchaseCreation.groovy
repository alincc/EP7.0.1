package com.elasticpath.rest.ws.fixture.alias

class PurchaseCreation {

	def client

	def PurchaseCreation(client) {
		this.client = client

		this.client.alias(this.&submitPurchase)
		this.client.alias(this.&selectAnyShippingOption)
		this.client.alias(this.&createRandomAddress)
		this.client.alias(this.&selectShippingAddressByPostalCode)
		this.client.alias(this.&selectAnyDestination)
		this.client.alias(this.&selectAnyBillingInfo)
		this.client.alias(this.&selectShippingOptionByName)
	}

	def submitPurchase() {
		client.GET("/")
				.defaultcart()
				.order()
				.purchaseform()
				.submitorderaction()
	}

	def selectAnyShippingOption() {
		client.GET("/")
				.defaultcart()
				.order()
				.deliveries()
				.element()
				.shippingoptioninfo()
				.selector()

		def choiceExists = false;
		client.body.links.find {
			if (it.rel == "choice") {
				choiceExists = true
			}
		}
		if (choiceExists) {
			client.choice().selectaction()
		}

	}

	def createRandomAddress() {
		def randomAddress = UUID.randomUUID().toString() + "random street"
		client.GET("/")
				.defaultprofile()
				.addresses()
				.addressform()
				.createaddressaction(address:["country-name": "CA", "locality":"Vancouver", "postal-code": "V7V7V7", "region":"BC", "street-address":randomAddress],name:["family-name":"itest","given-name":"generated"])
	}

	def selectAnyDestination() {
		client.GET("/")
				.defaultcart()
				.order()
				.deliveries()
				.element()
				.destinationinfo()
				.selector()
				.choice()
				.selectaction()
	}

	def selectAnyBillingInfo() {
		client.GET("/")
				.defaultcart()
				.order()
				.billingaddressinfo()
				.selector()
				.choice()
				.selectaction()
	}

	def selectShippingAddressByPostalCode(postalCode) {
		client.GET("/")
				.defaultcart()
				.order()
				.deliveries()
				.findElement {
					element ->
						def destinationinfo = element.destinationinfo()
						followSelectedAddress(destinationinfo, postalCode)
			  }

		client.GET("/").defaultcart()
	}

	def followSelectedAddress(destinationinfo, postalCode) {
		destinationinfo.selector()
			.findChoice {
				addressOption ->
					def description = addressOption.description()
					description["address"]["postal-code"] == postalCode
		}
		.selectaction()
		.follow()
	}

	def selectShippingOptionByName(shippingServiceName) {
        client.GET("/")
				.defaultcart()
                .order()
                .deliveries()
                .element()
                .shippingoptioninfo()
                .selector()
                .findChoice {
					shippingService ->
						def description = shippingService.description()
						description["display-name"] == shippingServiceName
				}
        		.selectaction()
    }

}
