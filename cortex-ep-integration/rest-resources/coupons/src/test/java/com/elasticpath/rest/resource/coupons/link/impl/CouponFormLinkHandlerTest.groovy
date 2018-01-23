package com.elasticpath.rest.resource.coupons.link.impl

import static com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels.APPLY_COUPON_ACTION_REL
import static com.elasticpath.rest.schema.SelfFactory.createSelf

import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.coupons.CouponEntity
import com.elasticpath.rest.schema.ResourceLinkFactory
import com.elasticpath.rest.schema.ResourceState

@RunWith(MockitoJUnitRunner)
class CouponFormLinkHandlerTest {

	@InjectMocks
	CouponFormLinkHandler handler

	def link = '/otherResource/scope/id'
	def formLink = "$link/form"
	def applyCouponLink = ResourceLinkFactory.createUriRel(
			link,
			APPLY_COUPON_ACTION_REL
	)

	def couponEntity = CouponEntity.builder()
			.build()
	def resourceState = ResourceState.Builder.create(couponEntity)
			.withSelf(createSelf(formLink))
			.build()

	@Test
	void testLinksAreCorrectWhenTransformingCouponEntityFromOtherRepresentation() {
		def result = handler.getLinks(resourceState)[0]

		assert applyCouponLink == result
	}
}
