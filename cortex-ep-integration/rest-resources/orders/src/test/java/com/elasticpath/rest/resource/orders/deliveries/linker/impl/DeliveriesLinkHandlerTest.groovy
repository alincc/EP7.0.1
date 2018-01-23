package com.elasticpath.rest.resource.orders.deliveries.linker.impl

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.DELIVERY
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER
import static com.elasticpath.rest.resource.orders.deliveries.DeliveryConstants.DELIVERY_LIST_NAME
import static com.elasticpath.rest.schema.util.ElementListFactory.createElementsOfList
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
import com.elasticpath.rest.resource.orders.deliveries.DeliveryLookup
import com.elasticpath.rest.schema.ResourceLink
import com.elasticpath.rest.schema.ResourceLinkFactory
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.schema.uri.DeliveryListUriBuilder
import com.elasticpath.rest.schema.uri.DeliveryListUriBuilderFactory
import com.elasticpath.rest.schema.uri.OrdersUriBuilder
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory
import com.elasticpath.rest.uri.URIUtil

@RunWith(MockitoJUnitRunner)
class DeliveriesLinkHandlerTest {

	private static final String DELIVERY_ID_2 = "delivery_id_2";
	private static final String DELIVERY_ID_1 = "delivery_id_1";
	private static final String ORDER_RESOURCE = "orders";
	private static final String SCOPE = "scope";
	private static final String ORDER_ID = "order_id";
	private static final String DELIVERIES = "deliveries";
	private static final String ORDER_URI = '/ordersUri'
	private static final String DELIVERIES_URI = URIUtil.format(ORDER_URI, DELIVERIES);

	@Mock
	DeliveryLookup deliveryLookup

	@Mock
	DeliveryListUriBuilderFactory deliveryListUriBuilderFactory

	@Mock
	OrdersUriBuilderFactory ordersUriBuilderFactory

	@InjectMocks
	DeliveriesLinkHandler handler = new DeliveriesLinkHandler()

	LinksEntity linksEntity = LinksEntity.builder()
			.withName(DELIVERY_LIST_NAME)
			.build()
	ResourceState<LinksEntity> resourceState = ResourceState.Builder.create(linksEntity)
			.withScope(SCOPE)
			.build()

	Collection<String> deliveryIds = [DELIVERY_ID_1, DELIVERY_ID_2];
	Collection<ResourceLink> deliveryLinks = createElementsOfList(
			DELIVERIES_URI,
			deliveryIds,
			DELIVERY.id()
	);

	ResourceLink orderLink = ResourceLinkFactory.create(ORDER_URI, ORDER.id(), "order", "deliveries");

	@Before
	void setUp() {
		'Given deliveries uri'(DELIVERIES_URI)
		'Given order uri'(ORDER_URI)

		given(deliveryLookup.getDeliveryIds(anyString(), anyString()))
				.willReturn(createReadOK(deliveryIds))
	}

	@Test
	void 'Given incorrect links entity, when linking, should build no links'() {
		LinksEntity linksEntity = LinksEntity.builder()
				.withName('INVALID_NAME')
				.build()
		ResourceState<LinksEntity> resourceState = ResourceState.Builder.create(linksEntity)
				.withScope(SCOPE)
				.build()

		def result = handler.getLinks(resourceState) as List

		assert result.isEmpty()
	}

	@Test
	void 'Given deliveries link, when linking, should build correct link'() {
		def result = handler.getLinks(resourceState) as List

		assert deliveryLinks == result[0..1]
	}

	@Test
	void 'Given orders link, when linking, should build correct link'() {
		def result = handler.getLinks(resourceState)[2]

		assert orderLink == result
	}

	void 'Given deliveries uri'(String uri) {

		def builder = mock(DeliveryListUriBuilder)

		given(deliveryListUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setSourceUri(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
	}

	void 'Given order uri'(String uri) {

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
