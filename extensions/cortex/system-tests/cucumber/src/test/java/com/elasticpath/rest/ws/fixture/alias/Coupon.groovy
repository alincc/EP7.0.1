package com.elasticpath.rest.ws.fixture.alias

class Coupon {

	def client

	def Coupon(client) {
		this.client = client;
		client.alias(this.&applyCoupon)
		client.alias(this.&getCouponInfo)
		client.alias(this.&removeCoupon)
	}

	def applyCoupon(couponCode) {
		client.GET("/")
				.defaultcart()
				.order()
				.couponinfo()
				.couponform()
				.applycouponaction(["code": couponCode])
	}

	def getCouponInfo() {
		client.GET("/")
				.defaultcart()
				.order()
				.couponinfo()
	}
	
	def removeCoupon(couponcode) {
		getCouponInfo()
	
		def criteria = { coupon ->
			coupon["code"] == couponcode
		}
		client.findLink("coupon", criteria)
	
		def couponUri = client.body.self.uri
	
		client.DELETE(couponUri)
	}
}