package com.elasticpath.rest.resource.orders.linker.impl

import static com.elasticpath.rest.definition.carts.CartsMediaTypes.CART
import static com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels.CART_REL
import static com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels.ORDER_REV
import static com.elasticpath.rest.schema.ResourceLinkFactory.create
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.mock

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.orders.OrderEntity
import com.elasticpath.rest.schema.ResourceLink
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.schema.uri.CartsUriBuilder
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory

@RunWith(MockitoJUnitRunner)
class CartToOrderLinkHandlerTest {

	String cartUri = "/cartUri"
	ResourceLink expectedCartLink = create(
			cartUri,
			CART.id(),
			CART_REL,
			ORDER_REV
	)

	@Mock
	CartsUriBuilderFactory cartsUriBuilderFactory

	@InjectMocks
	CartToOrderLinkHandler handler

	OrderEntity orderEntity = OrderEntity.builder()
			.withOrderId('orderId')
			.withCartId('cartId')
			.build()
	ResourceState<OrderEntity> resourceState = ResourceState.Builder.create(orderEntity)
			.build()

	@Before
	void setUp() {
		'Given cart uri'(cartUri)
	}

	@Test
	void 'Given cartLink, should create link correctly'() {
		def result = handler.getLinks(resourceState)

		assert expectedCartLink == result[0]
	}

	void 'Given cart uri'(String uri) {
		def builder = mock(CartsUriBuilder)

		given(cartsUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setCartId(anyString()))
				.willReturn(builder)
		given(builder.setScope(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
	}
}
