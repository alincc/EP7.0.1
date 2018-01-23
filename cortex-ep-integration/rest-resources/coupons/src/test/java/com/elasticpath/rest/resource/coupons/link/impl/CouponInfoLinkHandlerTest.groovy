package com.elasticpath.rest.resource.coupons.link.impl

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK
import static com.elasticpath.rest.definition.coupons.CouponsMediaTypes.COUPON
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER
import static com.elasticpath.rest.resource.coupons.constant.CouponsConstants.COUPON_INFO_NAME
import static com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels.APPLY_COUPON_FORM_REL
import static com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels.COUPON_REL
import static com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels.ORDER_REL
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.mock

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.controls.InfoEntity
import com.elasticpath.rest.resource.coupons.integration.OrderCouponsLookupStrategy
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.schema.uri.CouponsUriBuilder
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory
import com.elasticpath.rest.schema.uri.OrdersUriBuilder
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory

@RunWith(MockitoJUnitRunner)
class CouponInfoLinkHandlerTest {

	@Mock
	CouponsUriBuilderFactory couponsUriBuilderFactory

	@Mock
	OrderCouponsLookupStrategy orderCouponsLookupStrategy

	@Mock
	OrdersUriBuilderFactory ordersUriBuilderFactory

	@InjectMocks
	CouponInfoLinkHandler handler

	def infoEntity = InfoEntity.builder()
			.withName(COUPON_INFO_NAME)
			.build()
	def resourceState = ResourceState.Builder.create(infoEntity)
			.build()

	String couponsUri = '/couponsUri'
	def couponsLink = createNoRev(
			couponsUri,
			COUPON.id(),
			COUPON_REL
	)

	String ordersUri = '/ordersUri'
	def ordersLink = createNoRev(
			ordersUri,
			ORDER.id(),
			ORDER_REL
	)

	String couponsFormUri = '/couponsFormUri'
	def couponsFormLink = createNoRev(
			couponsFormUri,
			COUPON.id(),
			APPLY_COUPON_FORM_REL
	)

	@Before
	void setUp() {
		'Given coupons uri'(couponsUri)
		'Given orders uri'(ordersUri)

		given(orderCouponsLookupStrategy.findCouponIdsForOrder(anyString(), anyString()))
				.willReturn(createReadOK(['irrelevantCouponId'] as Collection))
	}

	@Test
	void 'Given invalid Info name, when linking, should not process'() {
		def infoEntity = InfoEntity.builder()
				.withName('not a valid name')
				.build()
		def resourceState = ResourceState.Builder.create(infoEntity)
				.build()

		def result = handler.getLinks(resourceState)

		assert [] == result
	}

	@Test
	void 'Given couponsUri, when linking, should build correct uri'() {
		def result = handler.getLinks(resourceState)

		assert couponsLink == result.find { link -> COUPON_REL == link.rel }
	}

	@Test
	void 'Given couponsFormUri, when linking, should build correct uri'() {
		'Given coupons form uri'(couponsFormUri)

		def result = handler.getLinks(resourceState)

		assert couponsFormLink == result.find { link -> APPLY_COUPON_FORM_REL == link.rel }
	}

	@Test
	void 'Given ordersUri, when linking, should build correct uri'() {
		def result = handler.getLinks(resourceState)

		assert ordersLink == result.find { link -> ORDER_REL == link.rel }
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

	void 'Given coupons form uri'(String uri) {
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

	void 'Given orders uri'(String uri) {
		def builder = mock(OrdersUriBuilder)

		given(ordersUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setOrderId(anyString()))
				.willReturn(builder)
		given(builder.setScope(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
	}
}
