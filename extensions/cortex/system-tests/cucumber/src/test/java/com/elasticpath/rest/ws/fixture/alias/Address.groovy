package com.elasticpath.rest.ws.fixture.alias


class Address {

    def client

    def Address(client) {
        this.client = client
        client.alias(this.&addNewUSAddressForZeroTaxState)
        client.alias(this.&getDefaultBillingAddress)
        client.alias(this.&getDefaultShippingAddress)
        client.alias(this.&addNewAddress)
    }

    def getDefaultBillingAddress() {
        client.GET("/")
                .defaultprofile()
                .addresses()
                .billingaddresses()
                .default()
    }

    def getDefaultShippingAddress() {
        client.GET("/")
                .defaultprofile()
                .addresses()
                .shippingaddresses()
                .default()
    }

    def addNewUSAddressForZeroTaxState() {
        client.GET("/")
                .defaultprofile()
                .addresses()
                .addressform()
                .createaddressaction(
                [
                        address: [
                                "street-address": "123 Main Street", "extended-address": "", locality: "Anchorage",
                                region          : "AK", "postal-code": "99501", "country-name": "US"
                        ],
                        name   : [
                                "family-name": "testFamilyName", "given-name": "testGivenName"
                        ]
                ]
        )
    }

    def addNewAddress(country, subcountry) {
        client.GET("/")
                .defaultprofile()
                .addresses()
                .addressform()
                .createaddressaction(
                [
                        address: [
                                "street-address": "123 Somestreet", "extended-address": "", locality: "somecity",
                                region          : subcountry, "postal-code": "555555", "country-name": country
                        ],
                        name   : [
                                "family-name": "testFamilyName", "given-name": "testGivenName"
                        ]
                ]
        )
        assert client.response.status == 201
    }
}
