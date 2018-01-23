package com.elasticpath.rest.resource.coupons.link.impl

import static com.elasticpath.rest.definition.coupons.CouponsMediaTypes.COUPON
import static com.elasticpath.rest.resource.coupons.constant.CouponsConstants.PURCHASES_FOR_COUPONS_LIST
import static com.elasticpath.rest.rel.ListElementRels.ELEMENT
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.mock

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.collections.LinksEntity
import com.elasticpath.rest.resource.coupons.integration.PurchaseCouponsLookupStrategy
import com.elasticpath.rest.schema.ResourceLinkFactory
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.schema.uri.CouponsUriBuilder
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory
import com.elasticpath.rest.schema.uri.PurchaseUriBuilder
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory

@RunWith(MockitoJUnitRunner)
class PurchaseCouponLinkHandlerTest {

	def couponId = '12345'

	@Mock
	CouponsUriBuilderFactory couponsUriBuilderFactory

	@Mock
	PurchaseCouponsLookupStrategy purchaseCouponsLookupStrategy

	@Mock
	PurchaseUriBuilderFactory purchaseUriBuilderFactory

	@InjectMocks
	PurchaseCouponLinkHandler handler

	def linksEntity = LinksEntity.builder()
			.withElementListId('purchaseId')
			.withName(PURCHASES_FOR_COUPONS_LIST)
			.build()

	def purchaseState = ResourceState.Builder.create(linksEntity)
			.withScope('scope')
			.build()

	def couponIds = [couponId]

	String purchasesUri = '/purchases'
	String couponsUri = '/coupons'
	def couponsLink = ResourceLinkFactory.createNoRev(
			couponsUri,
			COUPON.id(),
			ELEMENT,
	)

	@Before
	void setUp() {
		'Given coupons uri'(couponsUri)
		'Given purchase uri'(purchasesUri)

		given(purchaseCouponsLookupStrategy.getCouponsForPurchase(anyString(), anyString()))
				.willReturn(couponIds)
	}

	@Test
	void 'Given purchaseUri, when linking, should create purchases link'() {
		def result = handler.getLinks(purchaseState)[0]

		assert couponsLink == result
	}

	void 'Given coupons uri'(String uri) {
		def builder = mock(CouponsUriBuilder)

		given(couponsUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setCouponId(anyString()))
				.willReturn(builder)
		given(builder.setFormUri())
				.willReturn(builder)
		given(builder.setSourceUri(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
	}

	void 'Given purchase uri'(String uri) {
		def builder = mock(PurchaseUriBuilder)

		given(purchaseUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setPurchaseId(anyString()))
				.willReturn(builder)
		given(builder.setScope(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
	}
}
